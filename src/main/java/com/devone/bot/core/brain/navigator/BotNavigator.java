package com.devone.bot.core.brain.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.brain.navigator.simulator.BotTagsMakerSimulator;
import com.devone.bot.core.brain.navigator.tags.BotFovSliceTagger;
import com.devone.bot.core.brain.navigator.tags.BotNavigationTagsMaker;
import com.devone.bot.core.brain.perseption.scene.BotScanInfo;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.brain.perseption.scene.BotSceneSaver;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;
import com.devone.bot.core.utils.blocks.BotTagUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotNavigator {

    public static enum NavigationSuggestion {
        MOVE,
        CHANGE_DIRECTION,
    }

    private transient Bot bot;
    private boolean stuck = false;
    
    private boolean inDanger = false;

    private int stuckCount = 0;
    private NavigationSuggestion navigationSuggestion;
    private BotBlockData suggestedTarget;

    private List<BotBlockData> candidates;
    private BotPosition position;
    private transient BotBlockData target;
    private float bestYaw;

    public float getBestYaw() {
        return bestYaw;
    }

    public void setBestYaw(float bestYaw) {
        this.bestYaw = bestYaw;
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
        if (bot.getNPC() != null) {
            if (bot.getNPC().getEntity() != null) {
                Location loc = bot.getNPC().getEntity().getLocation();
                this.position = BotWorldHelper.locationToBotPosition(loc);
            } else if (bot.getNPC().getStoredLocation() != null) {
                Location loc = bot.getNPC().getStoredLocation();
                this.position = BotWorldHelper.locationToBotPosition(loc);
            }
        }
        return this.position;
    }

    public BotPositionSight getPositionSight() {
        if (bot.getNPC() != null) {
            if (bot.getNPC().getEntity() != null) {
                Location loc = bot.getNPC().getEntity().getLocation();
                return BotWorldHelper.locationToBotPositionSight(loc);
            } else if (bot.getNPC().getStoredLocation() != null) {
                Location loc = bot.getNPC().getStoredLocation();
                return BotWorldHelper.locationToBotPositionSight(loc);
            }
        }
        return null;
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
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), true, bot.getId() + " 🗺️ Target is set: " + tgt);
        }
        this.target = tgt;
    }

    public void setStuck(boolean stuck) {
        try {
            if (bot.getActiveTask() != null) {
                BotLogger.debug(BotUtils.getActiveTaskIcon(bot), true,
                        bot.getId() + " ❓ BotState: set Stuck=" + stuck);
                this.stuck = stuck;
                if (stuck) {
                    incrementStuckCount();
                    BotMemoryV2Utils.incrementCounter(bot, "stuckCount"); // ✅ глобально в memoryV2
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

    public List<BotBlockData> calculate(BotSceneData scene, double sightFov) {
        try {
            BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " 💻 Navigator calculation started");
        } catch (Exception ex) {
            BotLogger.debug("*", true, bot.getId() + " 💻 Navigator calculation started");
        }
    
        List<BotBlockData> result = new ArrayList<>();
        BotPositionSight botPos = getPositionSight();
        if (botPos == null) return result;
    

        int radius = BotConstants.DEFAULT_SCAN_RADIUS;
        Integer scanRadius = (Integer) BotMemoryV2Utils.readMemoryValue(bot, "navigation", "scanRadius");
            
        if(scanRadius!=null) {
            radius = scanRadius.intValue();
        }

        final int maxRadius = radius;

        BotMemoryV2Partition navPar = bot.getBrain().getMemoryV2().partition("navigation");
        BotMemoryV2Partition visPar = navPar.partition("visited", BotMemoryV2Partition.Type.MAP);
        Map<String, Object>  visited = visPar.getMap();
        
        // Tagging blocks
        long start = System.currentTimeMillis();
        int walkable = BotNavigationTagsMaker.tagWalkableBlocks(bot.getBrain().getSceneData().blocks);


        int reachable = BotNavigationTagsMaker.tagReachableBlocks(
                botPos,
                bot.getBrain().getSceneData().blocks,
                BotConstants.DEFAULT_NORMAL_SIGHT_FOV,
                BotConstants.DEFAULT_SCAN_RADIUS,
                BotConstants.DEFAULT_SCAN_HEIGHT
            );
           
        float bestYaw = BotTagsMakerSimulator.reachableFindBestYaw(botPos, bot.getBrain().getSceneData().blocks, 90, BotConstants.DEFAULT_SCAN_RADIUS, BotConstants.DEFAULT_SCAN_HEIGHT );            

        List<BotBlockData> reachableBlocks = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks,"reachable:*");
        //System.out.println("Reachable: " + reachableBlocks); 

        List<BotBlockData> walkableBlocks  = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks,"walkable:*");
        //System.out.println("Walkable: " + walkableBlocks); 

        // Валидируем цели and settting tag
        int reachableValidated = validateTargets(botPos, reachableBlocks);
        int walkableValidated = validateTargets(botPos, walkableBlocks);          

        updateNavigationSummary("reachable", reachable, reachableValidated);
        updateNavigationSummary("walkable",  walkable,  walkableValidated);
    
        // Логика выбора цели (приоритетная)
        List<BotBlockData> reachableValid = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks, "reachable:*, navigation:valid");

        if (!reachableValid.isEmpty()) {
            candidates = reachableValid;
            navigationSuggestion = NavigationSuggestion.MOVE;
            suggestedTarget = BlockUtils.findRandom(candidates);
        } else {
            List<BotBlockData> walkableValid = BotTagUtils.getTaggedBlocks(bot.getBrain().getSceneData().blocks, "walkable:*, navigation:valid");
            
            if (!walkableValid.isEmpty()) {
                candidates = walkableValid;
                navigationSuggestion = NavigationSuggestion.MOVE;
                suggestedTarget = BlockUtils.findRandom(candidates);
            } else {
                navigationSuggestion = NavigationSuggestion.CHANGE_DIRECTION;
                candidates = List.of();
                suggestedTarget = null;
            }
        }
    
        updateNavigationSummary("targets",  candidates != null ? candidates.size() : 0, candidates.size());
  
        boolean noTarget    = suggestedTarget    == null || candidates.isEmpty();

        // set The best YAW
        setBestYaw(bestYaw); // got from simulator       
        // Если нет ни одной полезной навигационной поверхности — считаем, что бот застрял
        boolean stuckNow = noTarget;
    
        setStuck(stuckNow);

        setInDanger(BotWorldHelper.isInDanger(bot));

        updateNavigationMemory();

        try {
            BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " 💻 Navigator calculation ended");
        } catch (Exception ex) {
            BotLogger.debug("*", true, bot.getId() + " 💻 Navigator calculation ended");
        }
    
        BotScanInfo scanInfo = new BotScanInfo(BotConstants.DEFAULT_SCAN_RADIUS, BotConstants.DEFAULT_SCAN_HEIGHT);
        BotSceneData sc_data_tagged = new BotSceneData(bot.getBrain().getSceneData().blocks, bot.getBrain().getSceneData().entities, botPos, scanInfo);

        long end = System.currentTimeMillis();
        System.out.println("[Timing] Tagging completed in " + (end - start) + " ms");

        /* 
        try {
            BotFovSliceTagger.tagFovSliceRemoveAll(bot.getBrain().getSceneData().blocks);
            BotSceneSaver.saveToJsonFile(BotConstants.PLUGIN_PATH_TMP + bot.getId()+"_scene_data_tagged.json_" + end, sc_data_tagged);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        */

        return candidates;
    }
    

    private int validateTargets(BotPositionSight botPos, List<BotBlockData> blocks) {
        if (blocks==null) return 0;
        
        int count = 0;

        for (BotBlockData target : blocks) {

            BotPosition pos = BlockUtils.fromBlock(target);
            Location loc = BotWorldHelper.botPositionToWorldLocation(pos);
    
            boolean canNavigate = bot.getNPC().getNavigator().canNavigateTo(loc);
            if (!canNavigate) continue;
       
            // 🛑 1. Исключаем блок под ногами
            if (BlockUtils.isSameBlockUnderfoot(botPos.toBlockData(), target)) continue;
    
            // 🛑 2. Слишком близко по XZ
            if (BlockUtils.distanceXZ(botPos.toBlockData(), target) < 2.0) continue;
    
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
    
        BotMemoryV2Partition navigation = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition summary = navigation.partition("summary", BotMemoryV2Partition.Type.MAP);
    
        BotMemoryV2Partition item = summary.partition(key, BotMemoryV2Partition.Type.MAP);
        item.put("calculated", calculated);
        item.put("confirmed", confirmed);
    }
    

    public void updateNavigationMemory() {
        BotMemoryV2 memory = getMemory();
        if (memory == null) return;
    
        BotMemoryV2Partition navigation = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
    
        // ➤ Позиция и направление
        BotBlockData currentPos = getPosition().toBlockData();
        BotPositionSight sight = getPositionSight();
    
        navigation.put("position", currentPos != null ? currentPos.toCompactString() : null);
        navigation.put("yaw", sight != null ? sight.getYaw() : null);
        navigation.put("bestYaw", getBestYaw());
        navigation.put("target", this.target != null ? this.target.toCompactString() : null);
        navigation.put("suggestion", navigationSuggestion != null ? navigationSuggestion.name() : null);
        navigation.put("suggestedTarget", suggestedTarget != null ? suggestedTarget.toCompactString() : null);
    
        // ➤ Кандидаты
        BotMemoryV2Partition candidatesPartition = navigation.partition("candidates", BotMemoryV2Partition.Type.LIST);
        candidatesPartition.getList().clear();
    
        if (candidates != null) {
            for (BotBlockData pos : candidates) {
                candidatesPartition.addToList(pos.toCompactString());
            }
        }
    }
    
    public NavigationSuggestion getNavigationSuggestion() {
        return navigationSuggestion;
    }

    public void setNavigationSuggestion(NavigationSuggestion suggestion) {
        this.navigationSuggestion = suggestion;
    }

    public BotBlockData getSuggestedTarget() {
        return suggestedTarget;
    }

    public void setSuggestedTarget(BotBlockData suggested) {
        this.suggestedTarget = suggested;
    }

    public boolean navigate(float speed) {
        if (this.target == null) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), true,
                    bot.getId() + " 🗺️ Target is null. Navigation is not possible ");
            return false;
        } else {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), true,
                    bot.getId() + " 🗺️ Runtime Target position: " + this.target);

            if (navigationSuggestion == NavigationSuggestion.MOVE) {
                BotPosition movePos = new BotPosition(this.target.getPosition());
                BotMoveTaskParams mvParams = new BotMoveTaskParams();
                mvParams.setTarget(movePos);
                BotMoveTask moveTask = new BotMoveTask(bot);
                moveTask.setParams(mvParams);
                BotTaskManager.push(bot, moveTask);
            } else {

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
                BotTeleportTaskParams params = new BotTeleportTaskParams();
                params.setPosition(suggestedTarget.getPosition());
                tp.setParams(params);
                BotTaskManager.push(bot, tp);
            }

            Location loc = BotWorldHelper.botPositionToWorldLocation(this.target.getPosition());
            return bot.getNPC().getNavigator().canNavigateTo(loc);
        }
    }
}
