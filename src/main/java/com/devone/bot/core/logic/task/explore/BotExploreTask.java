package com.devone.bot.core.logic.task.explore;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.core.logic.navigation.selectors.BotBlockSelector;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.explore.params.BotExploreTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.navigation.BotNavigationUtils;
import com.devone.bot.utils.scene.BotSceneData;

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

        BotLogger.info("🔶", isLogging(), bot.getId() + " Exploring with radius: " + scanRadius);

        setObjective(params.getObjective());

        if(params.isPickup()) {
            bot.pickupNearbyItems();
        }

        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();

        if (sceneData == null) {
            BotLogger.info("❌", isLogging(), bot.getId() + " No scene data available.");
            this.stop();
            return;
        }

        BotLocation botPos = bot.getBrain().getCurrentLocation();

        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, botPos);

        // Фильтрация целей (без изменения оригинальной коллекции)
        //List<BotBlockData> validGoals = new ArrayList<>();
        //for (BotBlockData goal : context.reachableGoals) {
        //    if (!bot.getBrain().getMemory().isMemorized(goal, MemoryType.VISITED)) {
        //        validGoals.add(goal);
        //    }
        //}


        // Если осталась только одна или меньше цели, бот застрял
        //int totalGoals = validGoals.size();
        //BotLogger.info(icon, isLogging(), "Valid Navigation Goals:" + totalGoals);

        //BotBlockData navGoal = null;

        //if (totalGoals <= 1) {
        //    // Бот застрял
        //    bot.getBrain().setStuck(true);
        //    BotLogger.info("🎯", this.isLogging(), "The bot " + bot.getId() + " is stuck!");
        //    stop();
        //    return;
        //} else {
        //    // Выбираем первую цель из отфильтрованного списка
        //    navGoal = validGoals.get(0);
        //}

        BotBlockData target = BotBlockSelector.pickRandomTarget(context.reachable);

        if (target != null) {
            BotLogger.info("🎯", isLogging(), bot.getId() + " Target: " + target);
            bot.getBrain().setTargetLocation(target);
            BotNavigationUtils.navigateTo(bot, bot.getBrain().getTargetLocation(), 1);

            // bot.getBrain().getMemory().memorize(navGoal, MemoryType.VISITED); // Запоминаем посещенную цель
        
        } else {
            BotLogger.info("🎯", isLogging(), bot.getId() + " No valid goal found.");
        }

        if (getElapsedTime() > 3 * BotConstants.DEFAULT_TASK_TIMEOUT) {
            BotLogger.info("⏱️", isLogging(), bot.getId() + " Task timeout: " + getElapsedTime());
            this.stop();
        }
    }


    @Override
    public void stop() {
        BotLogger.info("✅", isLogging(), "Exploration task completed for " + bot.getId());
        super.stop();
    }

}
