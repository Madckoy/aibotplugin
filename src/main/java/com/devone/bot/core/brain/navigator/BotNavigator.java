package com.devone.bot.core.brain.navigator;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.brain.navigator.selector.BotBestTargetSelector;
import com.devone.bot.core.brain.navigator.simulator.BotSimulatorResult;
import com.devone.bot.core.brain.navigator.simulator.BotTagsMakerSimulator;
import com.devone.bot.core.brain.navigator.tags.BotNavigationTagsMaker;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotTagUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotNavigator {

    private transient Bot bot;
    private boolean stuck = false;
    
    private boolean inDanger = false;

    private int stuckCount = 0;
    private BotActionSuggestion.Suggestion actionSuggestion;
    private BotBlockData suggestedTarget;

    private List<BotBlockData> candidates;
    private BotPosition position;
    private transient BotBlockData target;

    private boolean calculating = false;

    public boolean isCalculating() {
        return calculating;
    }

    public void setCalculating(boolean calculating) {
        this.calculating = calculating;
    }

    public BotNavigator() {
        this.position = null;
        this.target = null;
    }

    public BotNavigator(Bot owner) {
        this();
        this.bot = owner;
        this.position = getPosition();
    }

    private BotMemoryV2 getMemory() {
        return bot.getBrain().getMemoryV2();
    }

    public List<BotBlockData> getCandidates() {
        return candidates;
    }

    public BotPosition getPosition() {
        Location loc = getEffectiveLocation();
        if (loc != null) {
            this.position = BotWorldHelper.locationToBotPosition(loc);
        }
        return this.position;
    }

    private Location getEffectiveLocation() {
        if (bot.getNPC() == null) return null;

        if (bot.getNPC().isSpawned() && bot.getNPC().getEntity() != null) {
            return bot.getNPC().getEntity().getLocation(); // 🔥 Always up-to-date
        }

        return bot.getNPC().getStoredLocation(); // Fallback
    }

    public boolean isStuck() {
        return stuck;
    }

    public void setPosition(BotPosition pos) {
        this.position = pos;
    }

    public BotBlockData getTarget() {
        return target;
    }

    public void setTarget(BotBlockData tgt) {
        if (tgt != null) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(), bot.getId() + " 🗺️ Target is set: " + tgt);
        }
        this.target = tgt;
    }

    public void setStuck(boolean stuck) {
        try {
            if (bot.getActiveTask() != null) {
                BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                        bot.getId() + " ❓ BotState: set Stuck=" + stuck);
                this.stuck = stuck;
                if (stuck) {

                    incrementStuckCount();

                    BotMemoryV2Utils.incrementPartitionItem(bot, BotMemoryPartition.PartitionKey.STATS,
                                                                 BotMemoryItem.ItemKey.STUCKS);
                }
            }
        } catch (Exception ex) {
        }
    }

    public int getStuckCount() {
        return stuckCount;
    }

    public void incrementStuckCount() {
        this.stuckCount++;
    }

    public void resetStuckCount() {
        this.stuckCount = 0;
    }
  
    public boolean isInDanger() {
        return inDanger;
    }

    public void setInDanger(boolean inDanger) {
        this.inDanger = inDanger;
    }

    public BotSimulatorResult simulate(double sightFov, int scanRadius, int scanHeight) throws Exception {

        if(isCalculating()) {
            BotLogger.debug(bot.getActiveTask().getIcon(), bot.isLogged(), bot.getId() + " 💻 Navigator is simulating...");
            throw new Exception("Navigation is being simulated");
        };

        calculating = true;
        BotPosition botPos = getPosition();
        BotSimulatorResult res = BotTagsMakerSimulator.reachableFindBestYaw(botPos, bot.getBrain().getSceneData().blocks, sightFov, scanRadius, scanHeight );            
        calculating = false;        
        return res;
    }

    public List<BotBlockData> calculate(double sightFov, int scanRadius, int scanHeight) throws Exception{

        if(isCalculating()) {
            BotLogger.debug(bot.getActiveTask().getIcon(), bot.isLogged(), bot.getId() + " 💻 Navigator is calculating...");
            throw new Exception("Navigation is being calculated");
        };

        calculating = true;
        actionSuggestion = BotActionSuggestion.Suggestion.NONE;

        try {
            BotLogger.debug(bot.getActiveTask().getIcon(), bot.isLogged(), bot.getId() + " 💻 Navigator calculation started");
        } catch (Exception ex) {
            BotLogger.debug("❌ ", bot.isLogged(), bot.getId() + " 💻 Navigator calculation falsed");
        }
    
        List<BotBlockData> result = new ArrayList<>();
        BotPosition botPos = getPosition();
        if (botPos == null) { 
            calculating = false;
            return result;
        }
    

        int radius = scanRadius;

        Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readValueTyped(bot, 
                                    BotMemoryPartition.PartitionKey.NAVIGATION, 
                                    BotMemoryItem.ItemKey.SCAN_RADIUS, Integer.class);
            
        if(scanRadiusFromMem!=null) {
            radius = scanRadiusFromMem.intValue();
        }
      
        // Tagging blocks
        long start = System.currentTimeMillis();

        int walkable = BotNavigationTagsMaker.tagWalkableBlocks(bot.getBrain().getSceneData().blocks);
        int reachable = BotNavigationTagsMaker.tagReachableBlocks(
                botPos,
                bot.getBrain().getSceneData().blocks,
                sightFov,
                radius,
                scanHeight
            );
           
        List<BotBlockData> reachableBlocks = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks,"reachable:*");
        List<BotBlockData> walkableBlocks  = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks,"walkable:*");

        // Валидируем цели and settting tag
        int reachableValidated = validateTargets(botPos, reachableBlocks);
        int walkableValidated = validateTargets(botPos, walkableBlocks);          

        updateNavigationSummary(BotMemoryItem.ItemKey.REACHABLE.toString(), reachable, reachableValidated);
        updateNavigationSummary(BotMemoryItem.ItemKey.WALKABLE.toString(),  walkable,  walkableValidated);
    
        // Логика выбора цели (приоритетная)
        List<BotBlockData> reachableValid = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks, "reachable:*, navigation:valid");

        // 🧹 Убираем ранее посещённые блоки
        reachableValid = reachableValid.stream()
            .filter(b -> !BotMemoryV2Utils.isBlockVisited(bot, b))
            .toList();


        if (!reachableValid.isEmpty()) {
            candidates = reachableValid;
            actionSuggestion = BotActionSuggestion.Suggestion.NAVIGATION_MOVE;
            suggestedTarget = BotBestTargetSelector.selectRandom(candidates);
            
        } else {    
            // check if we could use walkable as fallback
            List<BotBlockData> walkableValid = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks, "walkable:*, navigation:valid");

            // 🧹 Убираем ранее посещённые блоки
            //walkableValid = reachableValid.stream()
            //.filter(b -> !BotMemoryV2Utils.isBlockVisited(bot, b))
            //.toList();

            if( !walkableValid.isEmpty() ) {
                candidates = walkableValid;
                actionSuggestion = BotActionSuggestion.Suggestion.NAVIGATION_MOVE;
                suggestedTarget = BotBestTargetSelector.selectRandom(candidates);
            } else {
              candidates = List.of();
              suggestedTarget = null;
            }
        } 
    
        updateNavigationSummary(BotMemoryItem.ItemKey.TARGETS.toString(),  candidates != null ? candidates.size() : 0, candidates.size());
  
        boolean noTarget = suggestedTarget    == null || candidates.isEmpty();

        // Если нет ни одной полезной навигационной поверхности — считаем, что бот застрял
        if(noTarget) {
            actionSuggestion = BotActionSuggestion.Suggestion.NAVIGATION_CHANGE_DIRECTION;
        } else {
            setTarget(suggestedTarget);
        }

        boolean stuckNow = noTarget;
    
        setStuck(stuckNow);

        setInDanger(BotWorldHelper.isInDanger(bot));

        updateNavigationMemory();

        try {
            BotLogger.debug(bot.getActiveTask().getIcon(), bot.isLogged(), bot.getId() + " 💻 Navigator calculation ended");
        } catch (Exception ex) {
            BotLogger.debug("*", bot.isLogged(), bot.getId() + " 💻 Navigator calculation ended");
        }
    

        long end = System.currentTimeMillis();
        
        BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                    bot.getId() + " ⏱ Tagging completed in " + (end - start) + " ms");

        
        calculating = false;
        return candidates;
    }
    

    private int validateTargets(BotPosition botPos, List<BotBlockData> blocks) {
        if (blocks==null) return 0;
        
        int count = 0;

        for (BotBlockData target : blocks) {

            BotPosition pos = BlockUtils.fromBlock(target);
            Location loc = BotWorldHelper.botPositionToWorldLocation(pos);
    
            boolean canNavigate = bot.getNPC().getNavigator().canNavigateTo(loc);
            if (!canNavigate) continue;
       
            // 🛑 1. Исключаем блок под ногами
            if (BlockUtils.isSameBlockUnderfoot(botPos.toPositionKey(), target.toPositionKey())) continue;
    
            // 🛑 2. Слишком близко по XZ
            if (BlockUtils.distanceXZ(botPos.toPositionKey(), target.toPositionKey()) < 2.0) continue;
    
            // ✅ 3. Воздух над блоком

            BotPosition posAbove = new BotPosition(target.getX(), target.getY() + 1, target.getZ());
            Block blockAbove = BotWorldHelper.botPositionToWorldBlock(posAbove);

            if (blockAbove.getType().isAir()) {
                target.addTag("navigation:valid");
                count++;
            }
        }
        return count;
    }
    
    private void updateNavigationSummary(String key, int calculated, int confirmed) {
        BotMemoryV2 memory = getMemory();
        if (memory == null) return;
    
        BotMemoryV2Partition navigation = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition summary    = navigation.partition(BotMemoryPartition.PartitionKey.SUMMARY.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition item       = summary.partition(key, BotMemoryV2Partition.Type.MAP);

        item.put(BotMemoryItem.ItemKey.CALCULATED.toString(), calculated);
        item.put(BotMemoryItem.ItemKey.CONFIRMED.toString(), confirmed);
    }
    

    public void updateNavigationMemory() {
        BotMemoryV2 memory = getMemory();
        if (memory == null) return;
    
    
        // ➤ Позиция, направление и рекомендации
        BotPosition currentPos = getPosition();
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.NAVIGATION, BotMemoryItem.ItemKey.POSITION, currentPos);
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.NAVIGATION, BotMemoryItem.ItemKey.TARGET, this.target);
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.NAVIGATION, BotMemoryItem.ItemKey.SUGGESTION, actionSuggestion);
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.NAVIGATION, BotMemoryItem.ItemKey.SUGGESTED_TARGET, suggestedTarget);

   
        // ➤ Кандидаты
        BotMemoryV2Partition navigation = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition candidatesPartition = navigation.partition(BotMemoryPartition.PartitionKey.CANDIDATES.toString(), BotMemoryV2Partition.Type.LIST);
   
        if (candidates != null) {
            candidatesPartition.getList().clear();
            for (BotBlockData pos : candidates) {
                candidatesPartition.addToList(pos.toCompactString());
            }
        }

    }
    
    public Suggestion getSuggestion() {
        return actionSuggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.actionSuggestion = suggestion;
    }

    public BotBlockData getSuggestedTarget() {
        return suggestedTarget;
    }

    public void setSuggestedTarget(BotBlockData suggested) {
        this.suggestedTarget = suggested;
    }

    public boolean navigate(float speed) throws Exception{

        if (this.target == null) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                    bot.getId() + " 🗺️ Target is null. Navigation is not possible ");
            return false;
        } else {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                    bot.getId() + " 🗺️ Runtime Target position: " + this.target);

            if (actionSuggestion == Suggestion.NAVIGATION_MOVE) {
                
                BotMoveTask moveTask = new BotMoveTask(bot);
                moveTask.setTarget(target.getPosition());
                
                BotTaskManager.push(bot, moveTask);
                Location loc = BotWorldHelper.botPositionToWorldLocation(this.target.getPosition());
                return bot.getNPC().getNavigator().canNavigateTo(loc);
            }

            if (actionSuggestion == Suggestion.NAVIGATION_TELEPORT) {
                if(suggestedTarget==null) {
                    if(getTarget()!=null) {
                        suggestedTarget = getTarget();
                    } else {
                        return false;
                    }
                }
                
                if(suggestedTarget==null) {
                    return false;
                }

                BotTeleportTask tp = new BotTeleportTask(bot, null);
                BotTeleportTaskParams params = tp.getParams();
                params.setPosition(suggestedTarget.getPosition());
                tp.setParams(params);
                BotTaskManager.push(bot, tp);
                return true;
            }
        }
        return false;
    }
}
