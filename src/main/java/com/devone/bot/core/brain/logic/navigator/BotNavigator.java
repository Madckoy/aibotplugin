package com.devone.bot.core.brain.logic.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationConextMaker;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.brain.logic.navigator.math.selector.BotBlockSelector;
import com.devone.bot.core.brain.logic.navigator.summary.BotNavigationSummaryItem;
import com.devone.bot.core.brain.memory.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
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
    private BotLocation location;
    private transient BotLocation navTarget; // Новое свойство для целевой локации

    private BotNavigationSummaryItem targets = new BotNavigationSummaryItem("targets");
    private BotNavigationSummaryItem reachable = new BotNavigationSummaryItem("reachable");
    private BotNavigationSummaryItem navigable = new BotNavigationSummaryItem("navigable");
    private BotNavigationSummaryItem walkable = new BotNavigationSummaryItem("walkable");

    private Map<String, BotNavigationSummaryItem> summary = new HashMap<String, BotNavigationSummaryItem>();

    public Map<String, BotNavigationSummaryItem> getSummary() {
        return summary;
    }

    public BotNavigator() {
        this.location = null; // Инициализируем с текущей локацией
        this.navTarget = null; // Начальное значение для targetLocation
        summary.put(targets.getId(), targets);
        summary.put(reachable.getId(), reachable);
        summary.put(navigable.getId(), navigable);
        summary.put(walkable.getId(), walkable);

    }

    public BotNavigator(Bot owner) {
        this();
        this.owner = owner;
        this.location = getLocation(); // Инициализируем с текущей локацией
        this.navTarget = null; // Начальное значение для targetLocation
    }

    public List<BotBlockData> getCandidates() {
        return candidates;
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public BotLocation getLocation() {
        if (owner.getNPC() != null) {
            // Получаем текущую локацию NPC
            BotLocation newLocation = BotWorldHelper.worldLocationToBotLocation(owner.getNPC().getStoredLocation());
            location = newLocation;
        } else {
            // Если NPC не найден, возвращаем null
            return null;
        }
        return location;
    }

    public boolean isStuck() {
        return stuck;
    }

    public void setLocation(BotLocation location) {
        this.location = location;
    }

    // Геттер и Сеттер для targetLocation
    public BotLocation getTarget() {
        return navTarget;
    }

    public void setTarget(BotLocation target) {

        if(target!=null) {
            BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " 🗺️ Wants to navigate to "
                    + target + " [ID: " + owner.getBrain().getCurrentTask().getIcon() +  
                    " " + owner.getBrain().getCurrentTask().getClass().getSimpleName() +"]");
        }

        this.navTarget = target;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
        BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " ❓ BotState: set Stuck=" + stuck);
        incrementStuckCount();
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

    private int loopTargets(BotLocation botPos, String key, List<BotBlockData> targets) {

        int targetable = 0;

        if(targets==null) {
            return targetable;
        }

        for (int i = 0; i < targets.size(); i++) {
            BotBlockData target = targets.get(i);
            Location loc = BotWorldHelper.getWorldLocation(target);
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            if (canNavigate) {
                targetable++;
                target.setTargetable(true);
            } else {
                target.setTargetable(false);
            }
        }

        summary.get(key).setCalculated(targets.size());
        summary.get(key).setConfirmed(targetable);

        return targetable;
    }

    public List<BotBlockData> calculate(BotSceneData scene) {

        List<BotBlockData> result = new ArrayList<BotBlockData>();

        BotLocation botPos = getLocation();

        BotNavigationContext context = BotNavigationConextMaker.getSceneContext(botPos, scene.blocks, scene.entities);

        if(context==null) {
            // context does not present yet
            BotLogger.debug(owner.getActiveTask().getIcon(), true, 
                owner.getId() + " ⚠️ Navigation error: Scene Context is not ready");

                setStuck(true); // stuck somewhere
            
                return result;
        }

        int targets    = loopTargets(botPos, "targets",   context.targets);
        int reachable  = loopTargets(botPos, "reachable", context.reachable);
        int navigable  = loopTargets(botPos, "navigable", context.navigable);
        int walkable   = loopTargets(botPos, "walkable",  context.walkable);

        boolean hardStuck = false;
        boolean softStuck = false;
        
        setStuck(false);

        if (targets == 0) {
            softStuck = true;
            if (reachable == 0) {
                softStuck = true;
                if (navigable == 0) {
                    if (walkable == 0) {
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
        
        suggested = BotBlockSelector.selectRandomTarget(candidates);

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

        if(this.navTarget!=null) {

            BotLogger.debug(owner.getActiveTask().getIcon(), true, 
                owner.getId() + " 🗺️ Target is null. Navigation is not possible " + " [ID: " + owner.getBrain().getCurrentTask().getIcon() + 
                           " " + owner.getBrain().getCurrentTask().getClass().getSimpleName() +" ]");
        
            BotLogger.debug(owner.getActiveTask().getIcon(), true,
                owner.getId() + " 🗺️ Runtime Target Location: " + this.navTarget + " [ID: " + owner.getBrain().getCurrentTask().getIcon() + 
                " " + owner.getBrain().getCurrentTask().getClass().getSimpleName() +" ]");


            BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();

            moveTaskParams.setTarget(this.navTarget);
            moveTaskParams.setSpeed(speed);

            BotMoveTask moveTask = new BotMoveTask(owner);
            moveTask.setParams(moveTaskParams);

            BotTaskManager.push(owner, moveTask);

            Location loc = BotWorldHelper.getWorldLocation(this.navTarget);
            boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);
            return canNavigate;
        }

        return false;
    }
}
