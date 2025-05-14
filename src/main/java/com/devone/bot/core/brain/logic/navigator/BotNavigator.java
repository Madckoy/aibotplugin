package com.devone.bot.core.brain.logic.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotContextMakerHelper;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.brain.logic.navigator.math.selector.BotTargetSelectionMode;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
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
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotNavigator {

    public static enum NavigationSuggestion {
        MOVE,
        CHANGE_DIRECTION,
    }

    private transient Bot owner;
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

    BotTargetSelectionMode targetSelectionMode = BotTargetSelectionMode.RANDOM;

    public BotNavigator() {
        this.position = null;
        this.target = null;
    }

    public BotNavigator(Bot owner) {
        this();
        this.owner = owner;
        this.position = getPosition();
    }

    private BotMemoryV2 getMemory() {
        return owner.getBrain().getMemoryV2();
    }

    public List<BotBlockData> getCandidates() {
        return candidates;
    }

    public BotPosition getPosition() {
        if (owner.getNPC() != null) {
            if (owner.getNPC().getEntity() != null) {
                Location loc = owner.getNPC().getEntity().getLocation();
                this.position = BotWorldHelper.locationToBotPosition(loc);
            } else if (owner.getNPC().getStoredLocation() != null) {
                Location loc = owner.getNPC().getStoredLocation();
                this.position = BotWorldHelper.locationToBotPosition(loc);
            }
        }
        return this.position;
    }

    public BotPositionSight getPositionSight() {
        if (owner.getNPC() != null) {
            if (owner.getNPC().getEntity() != null) {
                Location loc = owner.getNPC().getEntity().getLocation();
                return BotWorldHelper.locationToBotPositionSight(loc);
            } else if (owner.getNPC().getStoredLocation() != null) {
                Location loc = owner.getNPC().getStoredLocation();
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
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true, owner.getId() + " 🗺️ Target is set: " + tgt);
        }
        this.target = tgt;
    }

    public BotTargetSelectionMode getTargetSelectionMode() {
        return targetSelectionMode;
    }

    public void setTargetSelectionMode(BotTargetSelectionMode mode) {
        this.targetSelectionMode = mode;
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                owner.getId() + " switched target selection mode ➔ " + mode.name());
    }

    public void setStuck(boolean stuck) {
        try {
            if (owner.getActiveTask() != null) {
                BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                        owner.getId() + " ❓ BotState: set Stuck=" + stuck);
                this.stuck = stuck;
                if (stuck) {
                    incrementStuckCount();
                    BotMemoryV2Utils.incrementCounter(owner, "stuckCount"); // ✅ глобально в memoryV2
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
            BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " 💻 Navigator calculation started");
        } catch (Exception ex) {
            BotLogger.debug("*", true, owner.getId() + " 💻 Navigator calculation started");
        }
    
        List<BotBlockData> result = new ArrayList<>();
        BotPositionSight botPos = getPositionSight();
        if (botPos == null) return result;
    

        int radius = BotConstants.DEFAULT_SCAN_RADIUS;
        Integer scanRadius = (Integer) BotMemoryV2Utils.readMemoryValue(owner, "navigation", "scanRadius");
            
        if(scanRadius!=null) {
            radius = scanRadius.intValue();
        }

        final int maxRadius = radius;

        BotNavigationContext context = BotContextMakerHelper.alignBotToMaxReachableYaw(botPos, scene.blocks, sightFov, radius, BotConstants.DEFAULT_SCAN_HEIGHT);

        // Валидируем цели
        List<BotBlockData> targetsSightedValidatedPos   = validateTargets(botPos, context.targets);
        List<BotBlockData> reachableSightedValidatedPos = validateTargets(botPos, context.reachable);
        List<BotBlockData> navigableSightedValidatedPos = validateTargets(botPos, context.navigable);
        List<BotBlockData> walkableSightedValidatedPos  = validateTargets(botPos, context.walkable);
    
        updateNavigationSummary("targets",   context.targets != null ? context.targets.size() : 0, targetsSightedValidatedPos.size());
        updateNavigationSummary("reachable", context.reachable != null ? context.reachable.size() : 0, reachableSightedValidatedPos.size());
        updateNavigationSummary("navigable", context.navigable != null ? context.navigable.size() : 0, navigableSightedValidatedPos.size());
        updateNavigationSummary("walkable",  context.walkable != null ? context.walkable.size() : 0, walkableSightedValidatedPos.size());
    
        // Логика выбора цели
        if (targetsSightedValidatedPos.size() > 1) {
            candidates = targetsSightedValidatedPos;
            navigationSuggestion = NavigationSuggestion.MOVE;
            suggestedTarget = BlockUtils.findNearestReachable(getPosition().toBlockData(), candidates);
        } else if (reachableSightedValidatedPos.size() > 1) {
            candidates = reachableSightedValidatedPos;
            navigationSuggestion = NavigationSuggestion.MOVE;
            suggestedTarget = BlockUtils.findNearestReachable(getPosition().toBlockData(), candidates);
        } else {
            List<BotBlockData> reachableFallback = Stream.concat(
                    navigableSightedValidatedPos.stream(),
                    walkableSightedValidatedPos.stream()
            ).filter(pos -> BlockUtils.isSoftReachable(getPosition().toBlockData(), pos, maxRadius))
             .collect(Collectors.toList());
    
            if (!reachableFallback.isEmpty()) {
                candidates = reachableFallback;
                navigationSuggestion = NavigationSuggestion.MOVE;
                suggestedTarget = BlockUtils.findNearestReachable(getPosition().toBlockData(), candidates);
            } else {
                navigationSuggestion = NavigationSuggestion.CHANGE_DIRECTION;
                candidates = List.of();
                suggestedTarget = null;
            }
        }
    
        // ➤ Централизованная проверка: цель — это текущая позиция
        if (suggestedTarget != null && BlockUtils.isSameBlockUnderfoot(getPosition().toBlockData(), suggestedTarget)) {
            BotLogger.debug("*", true, owner.getId() + " 🔁 Suggested Target is underfoot — forcing direction change");
            navigationSuggestion = NavigationSuggestion.CHANGE_DIRECTION;
            suggestedTarget = null;
            candidates = List.of();
        }
    
        boolean noTarget    = targetsSightedValidatedPos    == null || targetsSightedValidatedPos.isEmpty();
        boolean noReachable = reachableSightedValidatedPos  == null || reachableSightedValidatedPos.isEmpty();
        boolean noNavigable = navigableSightedValidatedPos  == null || navigableSightedValidatedPos.isEmpty();
        boolean noWalkable  = walkableSightedValidatedPos   == null || walkableSightedValidatedPos.isEmpty();
    
        // set The best YAW
        setBestYaw(context.bestYaw);
        // Если нет ни одной полезной навигационной поверхности — считаем, что бот застрял
        boolean stuckNow = noTarget && noReachable && noNavigable && noWalkable;
    
        setStuck(stuckNow);

        setInDanger(BotWorldHelper.isInDanger(owner));

        updateNavigationMemory();

        try {
            BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " 💻 Navigator calculation ended");
        } catch (Exception ex) {
            BotLogger.debug("*", true, owner.getId() + " 💻 Navigator calculation ended");
        }
    
        return candidates;
    }
    

    private List<BotBlockData> validateTargets(BotPositionSight botPos, List<BotBlockData> blocks) {
        List<BotBlockData> navigable = new ArrayList<>();
        if (blocks == null) return navigable;
    
        for (BotBlockData target : blocks) {

            BotPosition pos = BlockUtils.fromBlock(target);
            Location loc = BotWorldHelper.botPositionToWorldLocation(pos);
    
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            if (!canNavigate) continue;
       
            // 🛑 1. Исключаем блок под ногами
            if (BlockUtils.isSameBlockUnderfoot(botPos.toBlockData(), target)) continue;
    
            // 🛑 2. Слишком близко по XZ
            if (BlockUtils.distanceXZ(botPos.toBlockData(), target) < 2.0) continue;
    
            // ✅ 3. Воздух над блоком

            BotPosition posAbove = new BotPosition(target.getX(), target.getY() + 1, target.getZ());
            Block blockAbove = BotWorldHelper.botPositionToWorldBlock(posAbove);

            if (blockAbove.getType().isAir()) {
                navigable.add(target);
            }
        }
    
        return navigable;
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
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                    owner.getId() + " 🗺️ Target is null. Navigation is not possible ");
            return false;
        } else {
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                    owner.getId() + " 🗺️ Runtime Target position: " + this.target);

            if (navigationSuggestion == NavigationSuggestion.MOVE) {
                BotPosition movePos = new BotPosition(this.target.getPosition());
                BotMoveTaskParams mvParams = new BotMoveTaskParams();
                mvParams.setTarget(movePos);
                BotMoveTask moveTask = new BotMoveTask(owner);
                moveTask.setParams(mvParams);
                BotTaskManager.push(owner, moveTask);
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

                BotTeleportTask tp = new BotTeleportTask(owner, null);
                BotTeleportTaskParams params = new BotTeleportTaskParams();
                params.setPosition(suggestedTarget.getPosition());
                tp.setParams(params);
                BotTaskManager.push(owner, tp);
            }

            Location loc = BotWorldHelper.botPositionToWorldLocation(this.target.getPosition());
            return owner.getNPC().getNavigator().canNavigateTo(loc);
        }
    }
}
