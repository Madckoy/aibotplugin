package com.devone.bot.core.bot.brain.logic.task.explore;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.navigation.BotNavigationUtils;
import com.devone.bot.core.bot.brain.logic.navigator.BotNavigationPlannerWrapper;
import com.devone.bot.core.bot.brain.logic.navigator.scene.BotSceneContext;
import com.devone.bot.core.bot.brain.logic.navigator.selectors.BotBlockSelector;
import com.devone.bot.core.bot.brain.logic.task.BotTaskAutoParams;
import com.devone.bot.core.bot.brain.logic.task.IBotTaskParameterized;
import com.devone.bot.core.bot.brain.logic.task.explore.params.BotExploreTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.BotConstants;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;
import com.devone.bot.core.bot.brain.memory.MemoryType;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;

public class BotExploreTask extends BotTaskAutoParams<BotExploreTaskParams> {

    private int scanRadius;

    public BotExploreTask(Bot bot) {
        super(bot, BotExploreTaskParams.class);
    }

    public IBotTaskParameterized<BotExploreTaskParams> setParams(BotExploreTaskParams params) {

        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        this.scanRadius = params.getScanRadius(); // Извлекаем параметр

        return this;
    }

    @Override
    public void execute() {

        if (isPaused)
            return;

        BotLogger.debug("🔶", isLogging(), bot.getId() + " Exploring with radius: " + scanRadius);

        setObjective(params.getObjective());

        if(params.isPickup()) {
            bot.pickupNearbyItems();
        }

        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();

        if (sceneData == null) {
            BotLogger.debug("❌", isLogging(), bot.getId() + " No scene data available.");
            this.stop();
            return;
        }

        BotLocation botPos = bot.getNavigation().getLocation();

        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, botPos);

        boolean isStuck = BotNavigationUtils.detectIfStuck(bot);

        BotLogger.debug(icon, isLogging(), bot.getId() + " STUCK: " + isStuck);


        BotBlockData target = BotBlockSelector.pickRandomTarget(context.reachableGoals);

        BotLogger.debug(icon, isLogging(), bot.getId() + " TOTAL REACHABLE: " + context.reachableGoals.size());
        
        if (context.reachableGoals.size() <= 1) {
                // Бот застрял
                bot.getState().setStuck(true);
                BotLogger.debug("🎯", this.isLogging(), "The bot " + bot.getId() + " is stuck!");
                stop();
                return;
            } else {
                // Фильтрация целей (без изменения оригинальной коллекции)
                List<BotBlockData> validGoals = new ArrayList<>();
                for (BotBlockData goal : context.reachableGoals) {
                if (!bot.getBrain().getMemory().isMemorized(goal, MemoryType.VISITED)) {
                validGoals.add(goal);
                }
            }
            target = validGoals.get(0);
        }

        if (target != null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " Target: " + target);
            bot.getNavigation().setTarget(target);
            BotNavigationUtils.navigateTo(bot, bot.getNavigation().getTarget(), 1);

            bot.getBrain().getMemory().memorize(target, MemoryType.VISITED); // Запоминаем на ~30 минут посещенную цель навигации

        } else {
            BotLogger.debug(icon, isLogging(), bot.getId() + " No valid goal found.");
        }

        if (getElapsedTime() > 3 * BotConstants.DEFAULT_TASK_TIMEOUT) {
            BotLogger.debug("⏱️", isLogging(), bot.getId() + " Task timeout: " + getElapsedTime());
            this.stop();
        }
    }


    @Override
    public void stop() {
        BotLogger.debug("✅", isLogging(), "Exploration task completed for " + bot.getId());
        super.stop();
    }

}
