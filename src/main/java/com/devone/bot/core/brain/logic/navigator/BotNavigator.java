package com.devone.bot.core.brain.logic.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.devone.bot.core.Bot;

import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContextMaker;
import com.devone.bot.core.brain.logic.navigator.math.selector.BotPOISelector;
import com.devone.bot.core.brain.logic.navigator.summary.BotNavigationSummaryItem;
import com.devone.bot.core.brain.memory.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotNavigator {

    public static enum NavigationType {
        WALK,
        TELEPORT;
    }

    private transient Bot owner;
    private boolean stuck = false;
    private int stuckCount = 0;
    private NavigationType suggestion;
    private BotBlockData   suggested;

    private List<BotBlockData> candidates;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotPosition position;
    private transient BotPosition poi; // Новое свойство для целевой локации

    private BotNavigationSummaryItem targets = new BotNavigationSummaryItem("targets");
    private BotNavigationSummaryItem reachable = new BotNavigationSummaryItem("reachable");
    private BotNavigationSummaryItem navigable = new BotNavigationSummaryItem("navigable");
    private BotNavigationSummaryItem walkable = new BotNavigationSummaryItem("walkable");

    private Map<String, BotNavigationSummaryItem> summary = new HashMap<String, BotNavigationSummaryItem>();

    public Map<String, BotNavigationSummaryItem> getSummary() {
        return summary;
    }

    public BotNavigator() {
        this.position = null; // Инициализируем с текущей локацией
        this.poi = null; // Начальное значение для targetLocation
        summary.put(targets.getId(), targets);
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

    public List<BotBlockData> getCandidates() {
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

    public boolean isStuck() {
        return stuck;
    }

    public void setLocation(BotPosition pos) {
        this.position = pos;
    }

    // Геттер и Сеттер для targetLocation
    public BotPosition getPoi() {
        return poi;
    }

    public void setTarget(BotPosition target) {

        if(target!=null) {
            BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " 🗺️ Wants to navigate to "
                    + target + " [ID: " + owner.getBrain().getCurrentTask().getIcon() +  
                    " " + owner.getBrain().getCurrentTask().getClass().getSimpleName() +"]");
        }

        this.poi = target;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
        BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " ❓ BotState: set Stuck=" + stuck);
        if(stuck) {
            incrementStuckCount();
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

    private List<BotBlockData> loopTargets(BotPosition botPos, String key, List<BotBlockData> targets) {

        List<BotBlockData> navigable = new ArrayList<>();

        if(targets==null) {
            return navigable;
        }

        for (int i = 0; i < targets.size(); i++) {
            BotBlockData target = targets.get(i);
            Location loc = BotWorldHelper.botPositionToWorldLocation(target);
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            if (canNavigate) {
                BotBlockData data = new BotBlockData();
                data.setX(target.getX());
                data.setY(target.getY());
                data.setZ(target.getZ());
                data.setType(target.getType());
                data.setUUID(target.getUUID());
                data.setBot(target.isBot());
                navigable.add(data);
            } 
        }

        summary.get(key).setCalculated(targets.size());
        summary.get(key).setConfirmed(navigable.size());

        return navigable;
    }

    public List<BotBlockData> calculate(BotSceneData scene) {

        List<BotBlockData> result = new ArrayList<BotBlockData>();

        BotPosition botPos = getPosition();

        BotNavigationContext context = BotNavigationContextMaker.createSceneContext(botPos, scene.blocks, scene.entities);

        if(context==null) {
            // context does not present yet
            BotLogger.debug(owner.getActiveTask().getIcon(), true, 
                owner.getId() + " ⚠️ Navigation error: Scene Context is not ready");

                setStuck(true); // stuck somewhere
            
                return result;
        }

        List<BotBlockData> targets    = loopTargets(botPos, "targets",   context.targets);
        List<BotBlockData> reachable  = loopTargets(botPos, "reachable", context.reachable);
        List<BotBlockData> navigable  = loopTargets(botPos, "navigable", context.navigable);
        List<BotBlockData> walkable   = loopTargets(botPos, "walkable",  context.walkable);

        boolean hardStuck = false;
        boolean softStuck = false;
        
        setStuck(false);

        if (targets.size() == 0) {
            softStuck = true;
            if (reachable.size() == 0) {
                softStuck = true;
                if (navigable.size() == 0) {
                    if (walkable.size() == 0) {
                        // hard stuck
                        hardStuck = true;
                        suggestion = NavigationType.TELEPORT;
                    } else {
                        result = context.walkable;
                        suggestion = NavigationType.TELEPORT;
                    }
                } else {
                    result = context.navigable;
                    suggestion = NavigationType.TELEPORT;
                }
            } else {
                softStuck = false;
                result = context.reachable;
                suggestion = NavigationType.WALK;
            }
        } else {
            result = context.targets;
            suggestion = NavigationType.WALK;
        }

        if (hardStuck) {
            setStuck(true);
        }

        candidates = result;
        
        suggested = BotPOISelector.selectRandom(owner, candidates);

        return result;
    }

    public BotNavigationSummaryItem getNavigationSummaryItem(String key) {
        return summary.get(key);
    }

    public NavigationType getSuggestion() {
        return suggestion;
    }

    public void setRecomendation(NavigationType suggestion) {
        this.suggestion = suggestion;
    }

    public BotBlockData getSuggested() {
        return suggested;
    }

    public void setSuggested(BotBlockData suggested) {
        this.suggested = suggested;
    }

    public boolean navigate(float speed) {

        if(this.poi!=null) {

            BotLogger.debug(owner.getActiveTask().getIcon(), true, 
                owner.getId() + " 🗺️ POI is null. Navigation is not possible " + " [ID: " + owner.getBrain().getCurrentTask().getIcon() + 
                           " " + owner.getBrain().getCurrentTask().getClass().getSimpleName() +" ]");
        
            BotLogger.debug(owner.getActiveTask().getIcon(), true,
                owner.getId() + " 🗺️ Runtime POI position: " + this.poi + " [ID: " + owner.getBrain().getCurrentTask().getIcon() + 
                " " + owner.getBrain().getCurrentTask().getClass().getSimpleName() +" ]");


            BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();

            moveTaskParams.setTarget(this.poi);
            moveTaskParams.setSpeed(speed);

            BotMoveTask moveTask = new BotMoveTask(owner);
            moveTask.setParams(moveTaskParams);

            BotTaskManager.push(owner, moveTask);

            Location loc = BotWorldHelper.botPositionToWorldLocation(this.poi);
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            return canNavigate;
        }

        return false;
    }
}
