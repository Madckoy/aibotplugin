package com.devone.bot.core.logic.task.explore;

import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
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
     
        bot.pickupNearbyItems(params.isPickup());

        BotSceneData sceneData = bot.getBrain().getSceneData();

        if (sceneData == null) {
            BotLogger.info("❌", isLogging(), bot.getId() + " No scene data available.");
            this.stop();
            return;
        }

        BotLocation botPos = bot.getBrain().getCurrentLocation();



        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities,
                botPos);

        //BotBlockData navGoal = BotGeoSelector.pickRandomTarget(context.reachableGoals);

        // check if has more than one block to visit
        int totalGoals = context.reachableGoals.size();
        
        BotBlockData navGoal = null;
        
        if(totalGoals <= 1) {
            //the bot is stuck!
            
            bot.getBrain().setStuck(true);

            BotLogger.info("🎯", this.isLogging(), "The bot "+bot.getId() + " is stuck!");
            
            stop();
            
            return;

        } else {

            // check all the goals if visited recently
            List<BotBlockData> goals = context.reachableGoals;

            for (BotBlockData goal : goals) {
                boolean isCached = bot.getBrain().getMemory().isMemorized(goal);
                if(isCached) { continue; 
                } else {
                    navGoal = goal;
                    break;
                }
            }
        }

        BotLogger.info("🎯", isLogging(), bot.getId() + " Target: " + navGoal);
        bot.getBrain().setTargetLocation(navGoal);

        BotNavigationUtils.navigateTo(bot, bot.getBrain().getTargetLocation(), 1);
        
        bot.getBrain().getMemory().memorize(navGoal);

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
