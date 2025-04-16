package com.devone.bot.core.logic.task.explore;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.core.logic.navigation.selectors.BotBioSelector;
import com.devone.bot.core.logic.navigation.selectors.BotGeoSelector;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.attack.survival.BotSurvivalAttackTask;
import com.devone.bot.core.logic.task.attack.survival.params.BotSurvivalAttackTaskParams;
import com.devone.bot.core.logic.task.explore.params.BotExploreTaskParams;
import com.devone.bot.core.logic.task.hand.attack.BotHandAttackTask;
import com.devone.bot.core.logic.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.logic.task.sonar.BotSonar3DTask;
import com.devone.bot.core.logic.task.teleport.BotTeleportTask;
import com.devone.bot.core.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.navigation.BotNavigationUtils;
import com.devone.bot.utils.scene.BotSceneData;

public class BotExploreTask extends BotTask<BotExploreTaskParams> {

    private int scanRadius;
    private BotExploreTaskParams params;

    public BotExploreTask(Bot bot) {
        super(bot);
        this.params = new BotExploreTaskParams();
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        this.scanRadius = params.getScanRadius();
    }

    @Override
    public void execute() {
        if (isPaused) return;

        BotLogger.info("🔶", isLogging(), bot.getId() + " Exploring with radius: " + scanRadius);

        BotSonar3DTask sonar = new BotSonar3DTask(bot, scanRadius, scanRadius);
        sonar.execute();

        setObjective(params.getObjective());
        bot.pickupNearbyItems(params.shouldPickup());

        BotSceneData sceneData = bot.getRuntimeStatus().getSceneData();
        if (sceneData == null) {
            BotLogger.info("❌", isLogging(), bot.getId() + " No scene data available.");
            this.stop();
            return;
        }

        BotLocation botPos = bot.getRuntimeStatus().getCurrentLocation();
        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, botPos);

        BotBlockData goal = BotGeoSelector.pickRandomTarget(context.reachableGoals);
        BotBlockData animal = BotBioSelector.pickNearestTarget(context.entities, botPos);

        BotLogger.info("🎯", this.isLogging(), bot.getId() + " Total nav targets: " + context.reachableGoals);

        if (bot.getRuntimeStatus().isStuck()) {
            BotLogger.info("⦻", this.isLogging(), bot.getId() + " Bot is stuck.");
            if (animal != null) {
                BotLogger.info("⚔️", this.isLogging(), bot.getId() + " Inflicting Survival Strike to unstuck!");
                BotSurvivalAttackTaskParams params = new BotSurvivalAttackTaskParams(animal, 5.0);
                BotSurvivalAttackTask strikeTask = new BotSurvivalAttackTask(bot);
                strikeTask.setParams(params);

                bot.getLifeCycle().getTaskStackManager().pushTask(strikeTask);

                stop();
                return;
            } else {
                BotLogger.info("❌", this.isLogging(), bot.getId() + " No animal found to unstuck.");
                if (getElapsedTime() > BotConstants.DEFAULT_TASK_TIMEOUT) {
                    BotLogger.info("⏱️", this.isLogging(), bot.getId() + " Task timeout.");
                    BotBlockData fallback = BotGeoSelector.pickEmergencyTeleportTarget(bot.getRuntimeStatus().getCurrentLocation(),
                            context.reachableGoals, context.reachable, context.navigable, context.walkable);
                    if (fallback != null) {
                        BotLogger.info("🌀", isLogging(), bot.getId() + " Navigation impossible, fallback teleporting to: " + fallback);

                        // Создание параметров
                        BotTeleportTaskParams tpParams = new BotTeleportTaskParams(fallback);

                        // Создание задачи и передача параметров
                        BotTeleportTask tpTask = new BotTeleportTask(bot, null);
                        tpTask.setParams(tpParams);  // Теперь параметры корректно передаются
                        
                        bot.getLifeCycle().getTaskStackManager().pushTask(tpTask);

                        BotLogger.info("💡", this.isLogging(), bot.getId() + " Teleporting to fallback location: " + fallback);
                        this.stop();
                        return;
                    }
                }
                return;
            }
        }

        if (animal != null) {
            BotLogger.info("⚔️", this.isLogging(), bot.getId() + " Inflicting Attack to bring justice on: " + animal);

            // Создание параметров
            BotHandAttackTaskParams handParams = new BotHandAttackTaskParams(animal, 5.0);


            // Создание задачи и передача параметров
            BotHandAttackTask handTask = new BotHandAttackTask(bot);
            handTask.setParams(handParams);  // Теперь параметры корректно передаются
            bot.getLifeCycle().getTaskStackManager().pushTask(handTask);            
            return;
        }

        BotLogger.info("🎯", isLogging(), bot.getId() + " Target: " + goal);
        bot.getRuntimeStatus().setTargetLocation(goal);
        BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation(), 1);

        if (getElapsedTime() > 3 * BotConstants.DEFAULT_TASK_TIMEOUT) {
            BotLogger.info("⏱️", isLogging(), bot.getId() + " Task timeout: " + getElapsedTime());
            this.stop();
        }
    }

    @Override
    public void stop() {
        BotLogger.info("✅", isLogging(), "Exploration task completed for " + bot.getId());
    }

    public IBotTaskParameterized<BotExploreTaskParams> setParams(BotExploreTaskParams params)  {
        super.setParams(params);  // вызов родительского setParams()
        
        // Здесь параметры типа BotExploreTaskParams
        this.scanRadius = params.getScanRadius(); // Извлекаем параметр
    
        return this;
    }
}
