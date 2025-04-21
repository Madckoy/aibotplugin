package com.devone.bot.core.bot.brain.logic.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.navigator.BotNavigationPlannerWrapper;
import com.devone.bot.core.bot.brain.logic.navigator.scene.BotSceneContext;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.active.move.BotMoveTask;
import com.devone.bot.core.bot.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.bot.task.passive.BotTaskManager;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotNavigation {

    public static enum NavigationType {
        WALK,
        TELEPORT;
    }

    private transient Bot owner;
    private boolean stuck = false;
    private int stuckCount = 0;
    private NavigationType suggestion;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotLocation location;
    private transient BotLocation target; // Новое свойство для целевой локации

    private BotNavigationSummaryItem targets = new BotNavigationSummaryItem("targets");
    private BotNavigationSummaryItem reachable = new BotNavigationSummaryItem("reachable");
    private BotNavigationSummaryItem navigable = new BotNavigationSummaryItem("navigable");
    private BotNavigationSummaryItem walkable = new BotNavigationSummaryItem("walkable");

    private Map<String, BotNavigationSummaryItem> summary = new HashMap<String, BotNavigationSummaryItem>();

    public Map<String, BotNavigationSummaryItem> getSummary() {
        return summary;
    }

    public BotNavigation() {
        this.location = null; // Инициализируем с текущей локацией
        this.target = null; // Начальное значение для targetLocation
        summary.put(targets.getId(), targets);
        summary.put(reachable.getId(), reachable);
        summary.put(navigable.getId(), navigable);
        summary.put(walkable.getId(), walkable);

    }

    public BotNavigation(Bot owner) {
        this();
        this.owner = owner;
        this.location = getLocation(); // Инициализируем с текущей локацией
        this.target = null; // Начальное значение для targetLocation
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
        return target;
    }

    public void setTarget(BotLocation targetLocation) {
        String locText = "";
        if (targetLocation != null) {
            locText = targetLocation.toString();
        }

        BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " 🏃🏻‍♂️‍➡️ Wants to navigate to "
                + locText + " [ID: " + owner.getBrain().getCurrentTask().getIcon() + "]");

        this.target = targetLocation;
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

        BotLocation botPos = owner.getNavigation().getLocation();

        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(botPos, scene.blocks, scene.entities);

        int targetable = loopTargets(botPos, "targets", context.targets);
        int reachable = loopTargets(botPos, "reachable", context.reachable);
        int navigable = loopTargets(botPos, "navigable", context.navigable);
        int walkable = loopTargets(botPos, "walkable", context.walkable);

        boolean hardStuck = false;
        boolean softStuck = false;

        if (targetable == 0) {
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

        if (hardStuck && softStuck) {
            setStuck(true);
        }

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

    public boolean navigate(float speed) {
        // BotNavigationUtils.navigateTo(bot, bot.getNavigation().getTarget(), speed);

        BotLogger.debug(owner.getActiveTask().getIcon(), true,
                owner.getId() + " 🎯 Runtime Target Location: " + owner.getNavigation().getTarget().toString()
                        + " [ID: "
                        + owner.getBrain().getCurrentTask().getIcon() + "]");

        BotMoveTask moveTask = new BotMoveTask(owner);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();

        moveTaskParams.setTarget(target);
        moveTaskParams.setSpeed(speed);
        moveTask.setParams(moveTaskParams);

        BotTaskManager.push(owner, moveTask);

        Location loc = BotWorldHelper.getWorldLocation(target);
        boolean canNavigate = owner.getNPC().getNavigator().canNavigateTo(loc);

        return canNavigate;
    }
}
