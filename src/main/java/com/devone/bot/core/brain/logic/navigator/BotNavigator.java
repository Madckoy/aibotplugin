package com.devone.bot.core.brain.logic.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContextMaker;
import com.devone.bot.core.brain.logic.navigator.math.selector.PoiSelectionMode;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotNavigator {

    public static enum NavigationSuggestion {
        WALK,
        CHANGE_DIRECTION,
    }

    private transient Bot owner;
    private boolean stuck = false;
    
    private boolean inDanger = false;

    private int stuckCount = 0;
    private NavigationSuggestion navigationSuggestion;
    private BotPosition suggestedPoi;

    private List<BotPosition> candidates;
    private BotPosition position;
    private transient BotPosition poi;

    PoiSelectionMode poiSelectionMode = PoiSelectionMode.RANDOM;

    public BotNavigator() {
        this.position = null;
        this.poi = null;
    }

    public BotNavigator(Bot owner) {
        this();
        this.owner = owner;
        this.position = getPosition();
    }

    private BotMemoryV2 getMemory() {
        return owner.getBrain().getMemoryV2();
    }

    public List<BotPosition> getCandidates() {
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

    public BotPosition getPoi() {
        return poi;
    }

    public void setPoi(BotPosition poi) {
        if (poi != null) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true, owner.getId() + " 🗺️ POI/Target is set: " + poi);
        }
        this.poi = poi;
    }

    public PoiSelectionMode getPoiSelectionMode() {
        return poiSelectionMode;
    }

    public void setPoiSelectionMode(PoiSelectionMode mode) {
        this.poiSelectionMode = mode;
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                owner.getId() + " switched POI selection mode ➔ " + mode.name());
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

    public List<BotPosition> calculate(BotSceneData scene, double sightFov) {
        try {
            BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " 💻 Navigator calculation started");
        } catch (Exception ex) {
            BotLogger.debug("*", true, owner.getId() + " 💻 Navigator calculation started");
        }
    
        List<BotPosition> result = new ArrayList<>();
        BotPositionSight botPos = getPositionSight();
        if (botPos == null) return result;
    
        BotNavigationContext context = BotNavigationContextMaker.createSceneContext(
            botPos, scene.blocks, scene.entities, sightFov);
        if (context == null) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                    owner.getId() + " ⚠️ Navigation error: Scene Context is not ready");
            return result;
        }
    
        // Валидируем цели
        List<BotPosition> poiSightedValidatedPos       = validateTargets(botPos, context.poi);
        List<BotPosition> reachableSightedValidatedPos = validateTargets(botPos, context.reachable);
        List<BotPosition> navigableSightedValidatedPos = validateTargets(botPos, context.navigable);
        List<BotPosition> walkableSightedValidatedPos  = validateTargets(botPos, context.walkable);
    
        updateNavigationSummary("poi",       context.poi != null ? context.poi.size() : 0, poiSightedValidatedPos.size());
        updateNavigationSummary("reachable", context.reachable != null ? context.reachable.size() : 0, reachableSightedValidatedPos.size());
        updateNavigationSummary("navigable", context.navigable != null ? context.navigable.size() : 0, navigableSightedValidatedPos.size());
        updateNavigationSummary("walkable",  context.walkable != null ? context.walkable.size() : 0, walkableSightedValidatedPos.size());
    
        // Логика выбора цели
        if (poiSightedValidatedPos.size() > 1) {
            candidates = poiSightedValidatedPos;
            navigationSuggestion = NavigationSuggestion.WALK;
            suggestedPoi = BlockUtils.findNearestReachable(getPosition(), candidates);
        } else if (reachableSightedValidatedPos.size() > 1) {
            candidates = reachableSightedValidatedPos;
            navigationSuggestion = NavigationSuggestion.WALK;
            suggestedPoi = BlockUtils.findNearestReachable(getPosition(), candidates);
        } else {
            List<BotPosition> reachableFallback = Stream.concat(
                    navigableSightedValidatedPos.stream(),
                    walkableSightedValidatedPos.stream()
            ).filter(pos -> BlockUtils.isSoftReachable(getPosition(), pos))
             .collect(Collectors.toList());
    
            if (!reachableFallback.isEmpty()) {
                candidates = reachableFallback;
                navigationSuggestion = NavigationSuggestion.WALK;
                suggestedPoi = BlockUtils.findNearestReachable(getPosition(), candidates);
            } else {
                navigationSuggestion = NavigationSuggestion.CHANGE_DIRECTION;
                candidates = List.of();
                suggestedPoi = null;
            }
        }
    
        // ➤ Централизованная проверка: цель — это текущая позиция
        if (suggestedPoi != null && BlockUtils.isSameBlockUnderfoot(getPosition(), suggestedPoi)) {
            BotLogger.debug("*", true, owner.getId() + " 🔁 Suggested POI is underfoot — forcing direction change");
            navigationSuggestion = NavigationSuggestion.CHANGE_DIRECTION;
            suggestedPoi = null;
            candidates = List.of();
        }
    
        boolean noPoi       = poiSightedValidatedPos == null || poiSightedValidatedPos.isEmpty();
        boolean noReachable = reachableSightedValidatedPos == null || reachableSightedValidatedPos.isEmpty();
        boolean noNavigable = navigableSightedValidatedPos == null || navigableSightedValidatedPos.isEmpty();
        boolean noWalkable  = walkableSightedValidatedPos == null || walkableSightedValidatedPos.isEmpty();
    
        // Если нет ни одной полезной навигационной поверхности — считаем, что бот застрял
        boolean stuckNow = noPoi && noReachable && noNavigable && noWalkable;
    
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
    

    private List<BotPosition> validateTargets(BotPositionSight botPos, List<BotBlockData> blocks) {
        List<BotPosition> navigable = new ArrayList<>();
        if (blocks == null) return navigable;
    
        for (BotBlockData target : blocks) {
            BotPosition pos = BlockUtils.fromBlock(target);
            Location loc = BotWorldHelper.botPositionToWorldLocation(pos);
    
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            if (!canNavigate) continue;
    
            BotPosition npos = new BotPosition(target.getX(), target.getY(), target.getZ());
    
            // 🛑 1. Исключаем блок под ногами
            if (BlockUtils.isSameBlockUnderfoot(botPos, npos)) continue;
    
            // 🛑 2. Слишком близко по XZ
            if (BlockUtils.distanceXZ(botPos, npos) < 2.0) continue;
    
            // ✅ 3. Воздух над блоком
            BotPosition posAbove = new BotPosition(npos.getX(), npos.getY() + 1, npos.getZ());
            Block blockAbove = BotWorldHelper.botPositionToWorldBlock(posAbove);
            if (blockAbove.getType().isAir()) {
                navigable.add(npos);
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
        BotPosition currentPos = getPosition();
        BotPositionSight sight = getPositionSight();
    
        navigation.put("position", currentPos != null ? currentPos.toCompactString() : null);
        navigation.put("yaw", sight != null ? sight.getYaw() : null);
        navigation.put("target", poi != null ? poi.toCompactString() : null);
        navigation.put("suggestion", navigationSuggestion != null ? navigationSuggestion.name() : null);
        navigation.put("suggestedPoi", suggestedPoi != null ? suggestedPoi.toCompactString() : null);
    
        // ➤ Кандидаты
        BotMemoryV2Partition candidatesPartition = navigation.partition("candidates", BotMemoryV2Partition.Type.LIST);
        candidatesPartition.getList().clear();
    
        if (candidates != null) {
            for (BotPosition pos : candidates) {
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

    public BotPosition getSuggestedPoi() {
        return suggestedPoi;
    }

    public void setSuggestedPoi(BotPosition suggested) {
        this.suggestedPoi = suggested;
    }

    public boolean navigate(float speed) {
        if (this.poi == null) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                    owner.getId() + " 🗺️ POI is null. Navigation is not possible ");
            return false;
        } else {
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                    owner.getId() + " 🗺️ Runtime POI position: " + this.poi);

            if (navigationSuggestion == NavigationSuggestion.WALK) {
                BotPosition movePos = new BotPosition(this.poi);
                BotMoveTaskParams mvParams = new BotMoveTaskParams();
                mvParams.setTarget(movePos);
                BotMoveTask moveTask = new BotMoveTask(owner);
                moveTask.setParams(mvParams);
                BotTaskManager.push(owner, moveTask);
            } else {
                BotTeleportTask tp = new BotTeleportTask(owner, null);
                BotTeleportTaskParams params = new BotTeleportTaskParams();
                params.setPosition(suggestedPoi);
                tp.setParams(params);
                BotTaskManager.push(owner, tp);
            }

            Location loc = BotWorldHelper.botPositionToWorldLocation(this.poi);
            return owner.getNPC().getNavigator().canNavigateTo(loc);
        }
    }
}
