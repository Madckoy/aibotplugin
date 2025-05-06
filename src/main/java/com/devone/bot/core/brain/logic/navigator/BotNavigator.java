package com.devone.bot.core.brain.logic.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.devone.bot.core.Bot;

import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContextMaker;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotSightBuilder;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotSightFilter;
import com.devone.bot.core.brain.logic.navigator.math.selector.BotPOISelector;
import com.devone.bot.core.brain.logic.navigator.math.selector.PoiSelectionMode;
import com.devone.bot.core.brain.logic.navigator.summary.BotNavigationSummaryItem;
import com.devone.bot.core.brain.memory.scene.BotSceneData;
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
        WALK,
        CHANGE_DIRECTION,
        TELEPORT;
    }

    private transient Bot owner;
    private double fov = BotConstants.DEFAULT_SIGHT_FOV;
    private boolean stuck = false;
    private int stuckCount = 0;
    private NavigationSuggestion navigationSuggestion;
    private BotPosition suggestedPoi;

    private List<BotPosition> candidates;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotPosition position;
    private transient BotPosition poi; // Новое свойство для целевой локации

    private BotNavigationSummaryItem pois      = new BotNavigationSummaryItem("poi");
    private BotNavigationSummaryItem reachable = new BotNavigationSummaryItem("reachable");
    private BotNavigationSummaryItem navigable = new BotNavigationSummaryItem("navigable");
    private BotNavigationSummaryItem walkable  = new BotNavigationSummaryItem("walkable");

    PoiSelectionMode poiSelectionMode = PoiSelectionMode.SMART;

    private Map<String, BotNavigationSummaryItem> summary = new HashMap<String, BotNavigationSummaryItem>();

    public Map<String, BotNavigationSummaryItem> getSummary() {
        return summary;
    }

    public BotNavigator() {
        this.position = null; // Инициализируем с текущей локацией
        this.poi = null; // Начальное значение для targetLocation
        summary.put(pois.getId(), pois);
        summary.put(reachable.getId(), reachable);
        summary.put(navigable.getId(), navigable);
        summary.put(walkable.getId(), walkable);

    }

    public BotNavigator(Bot owner) {
        this();
        this.owner = owner;
        this.position = getPosition(); // Инициализируем с текущей локацией
        this.poi = null; // Начальное значение для targetLocation
    }

    public List<BotPosition> getCandidates() {
        return candidates;
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public BotPosition getPosition() {
        if (owner.getNPC() != null) {
            // Получаем текущую локацию NPC
            BotPosition newPosition = BotWorldHelper.locationToBotPosition(owner.getNPC().getStoredLocation());
            position = newPosition;
        } else {
            // Если NPC не найден, возвращаем null
            return null;
        }
        return position;
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public BotPositionSight getPositionSight() {
        return BotWorldHelper.locationToBotPositionSight(owner.getNPC().getStoredLocation());
    }
    

    public boolean isStuck() {
        return stuck;
    }

    public void setPosition(BotPosition pos) {
        this.position = pos;
    }

    // Геттер и Сеттер для targetLocation
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

        BotLogger.debug(
                BotUtils.getActiveTaskIcon(owner), true,
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
                }
            }
        } catch (Exception ex) {

        }
    }

    // Счётчик застревания
    public int getStuckCount() {
        return stuckCount;
    }

    public void incrementStuckCount() {
        this.stuckCount++;
    }

    public void resetStuckCount() {
        this.stuckCount = 0;
    }

    public List<BotPosition> calculate(BotSceneData scene) {

        List<BotPosition> result = new ArrayList<BotPosition>();

        BotPositionSight botPos = getPositionSight();

        BotNavigationContext context = BotNavigationContextMaker.createSceneContext(botPos, scene.blocks,
                scene.entities);

        if (context == null) {
            // context does not present yet
            BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
                    owner.getId() + " ⚠️ Navigation error: Scene Context is not ready");

            return result;
        }

        //apply POI filtering 

        float yaw = botPos.getYaw(); // если есть

        BotPosition eye = new BotPosition(botPos.getX(), botPos.getY(), botPos.getZ());
        List<BotBlockData> viewSector = BotSightBuilder.buildViewSectorBlocks(eye, yaw, BotConstants.DEFAULT_SCAN_RANGE+5.0, 
                                                                             BotConstants.DEFAULT_SCAN_DATA_SLICE_HEIGHT, 
                                                                             fov);
        
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true, owner.getId() + " ⚠️ POI calculted (in context):" + context.poi);

        List<BotBlockData>  poiSighted       = BotSightFilter.filter(context.poi, viewSector);
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
        owner.getId() + " ⚠️ POI Sighted:" + poiSighted);
        
        //List<BotBlockData>  reachableSighted = BotSightFilter.filter(context.reachable, viewSector);
        //List<BotBlockData>  navigableSighted = BotSightFilter.filter(context.navigable, viewSector);
        //List<BotBlockData>  walkableSighted  = BotSightFilter.filter(context.walkable, viewSector);

        List<BotPosition> poiSightedValidatedPos       = validateTargets(botPos, "poi", poiSighted);
        
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true,
        owner.getId() + " ⚠️ POI Validated:" + poiSightedValidatedPos);

        //List<BotPosition> reachableSightedValidatedPos = validateTargets(botPos, "reachable", reachableSighted);
        //List<BotPosition> navigableSightedValidatedPos = validateTargets(botPos, "navigable", navigableSighted);
        //List<BotPosition> walkableSightedValidatedPos  = validateTargets(botPos, "walkable", walkableSighted);

        if (poiSightedValidatedPos.size() <= 1) {
            navigationSuggestion = NavigationSuggestion.CHANGE_DIRECTION;
        } else {
            candidates = poiSightedValidatedPos;
            navigationSuggestion = NavigationSuggestion.WALK;

            if (poiSelectionMode == PoiSelectionMode.SMART) {
                suggestedPoi = BotPOISelector.selectSmart(owner, candidates, context);
            } else {
                suggestedPoi = BotPOISelector.selectRandom(candidates);
            }
            result = candidates;
        }

        return result;
    }

    private List<BotPosition> validateTargets(BotPositionSight botPos, String key, List<BotBlockData> blocks) {

        List<BotPosition> navigable = new ArrayList<>();

        if (blocks == null) {
            return navigable;
        }

        for (int i = 0; i < blocks.size(); i++) {
            BotBlockData target = blocks.get(i);

            BotPosition pos = BlockUtils.fromBlock(target);
            Location loc = BotWorldHelper.botPositionToWorldLocation(pos);

            // BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true, owner.getId() + " ⚠️
            // nav candidate (world) = " + loc);

            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);

            // BotLogger.debug(BotUtils.getActiveTaskIcon(owner), true, owner.getId() + " ⚠️
            // NPC POS = " + owner.getNPC().getStoredLocation());

            if (canNavigate) {
                BotPosition npos = new BotPosition();
                npos.setX(target.getX());
                npos.setY(target.getY());
                npos.setZ(target.getZ());

                // 🚫 Не добавляем саму точку под ботом
                if (botPos.distanceTo(npos) <= 1.5) {
                    continue;
                }
                
                BotPosition posAbove = new BotPosition(npos.getX(), npos.getY() + 1, npos.getZ());
                // check if location above the valid target is AIR
                Block blockAbove = BotWorldHelper.botPositionToWorldBlock(posAbove);
                if (blockAbove.getType().toString().equals(Material.AIR.toString()) ||
                        blockAbove.getType().toString().equals(Material.CAVE_AIR.toString()) ||
                        blockAbove.getType().toString().equals(Material.VOID_AIR.toString())) {
                    navigable.add(npos);
                }
            }
        }

        summary.get(key).setCalculated(blocks.size());
        summary.get(key).setConfirmed(navigable.size());

        return navigable;
    }

    public BotNavigationSummaryItem getNavigationSummaryItem(String key) {
        return summary.get(key);
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

            // BotExcavateTaskParams excvParams = new BotExcavateTaskParams();
            // BotExcavateTask excvTask = new BotExcavateTask(owner);
            // excvTask.setParams(excvParams);
            // excvTask.setDeffered(true); // delayed update() to prevent run before the
            // move
            // BotTaskManager.push(owner, excvTask);

            if (navigationSuggestion == NavigationSuggestion.WALK) {

                BotPosition movePosiiton = new BotPosition(this.poi);

                BotMoveTaskParams mv_params = new BotMoveTaskParams();
                mv_params.setTarget(new BotPosition(movePosiiton));
                BotMoveTask moveTask = new BotMoveTask(owner);
                moveTask.setParams(mv_params);
                BotTaskManager.push(owner, moveTask);

            } else {

                BotTeleportTask tp = new BotTeleportTask(owner, null);
                BotTeleportTaskParams params = new BotTeleportTaskParams();
                params.setPosition(suggestedPoi);
                tp.setParams(params);
                BotTaskManager.push(owner, tp);

            }
            Location loc = BotWorldHelper.botPositionToWorldLocation(this.poi);
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            return canNavigate;
        }
    }
}
