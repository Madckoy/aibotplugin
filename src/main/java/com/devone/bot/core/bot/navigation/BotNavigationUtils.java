package com.devone.bot.core.bot.navigation;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.scene.BotSceneData;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.core.logic.task.move.BotMoveTask;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.utils.logger.BotLogger;

public class BotNavigationUtils {

    public static boolean detectIfStuck(Bot bot) {

        BotLocation botPos = bot.getNavigation().getLocation();
        BotSceneData sceneData =  bot.getBrain().getMemory().getSceneData();

        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities,
                botPos);

        // check if has more than one block to visit
        int totalGoals = context.reachableGoals.size();
        BotLogger.debug("", true, "Total reachable goals:" + totalGoals);
        
        if(totalGoals <= 1) {
            //the bot is stuck!
            bot.getState().setStuck(true);
            BotLogger.debug("🚨", true, "The bot "+bot.getId() + " is stuck!");
            return true;
        } else {
          return false;
        }
    }
    public static void navigateTo(Bot bot, BotLocation target, float multiplier) {
        if(target==null) {
            BotLogger.debug("🏃🏻‍♂️‍➡️ ", true, bot.getId() + "Target navigation is null. Can't navigate!");
            return;
        }
        navigate(bot, target, multiplier);
    }

    private static void navigate(Bot bot, BotLocation target, float multiplier) {

        
        BotLogger.debug("🏃🏻‍♂️‍➡️ ", true, bot.getId() + "Wants to navigate to " + target.toString() + " [ID: " + bot.getBrain().getCurrentTask().getIcon() + "]");
        BotLogger.debug("🎯", true,  bot.getId() + "Runtime Target Location: " + bot.getNavigation().getTarget().toString() + " [ID: " + bot.getBrain().getCurrentTask().getIcon() + "]");


        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();

        moveTaskParams.setTarget(target);
        moveTaskParams.setSpeed(multiplier);
        moveTask.setParams(moveTaskParams);
        
        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);

    }

}
