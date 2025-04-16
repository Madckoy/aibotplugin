package com.devone.bot.utils.navigation;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.move.BotMoveTask;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.logger.BotLogger;


public class BotNavigationUtils {


    public static void navigateTo(Bot bot, BotLocation target, float multiplier) {
        navigate(bot, target, multiplier);
    }

    private static void navigate(Bot bot, BotLocation target, float multiplier) {

        
        BotLogger.info("🏃🏻‍♂️‍➡️ ", true, bot.getId() + "Wants to navigate to " + target.toString() + " [ID: " + bot.getRuntimeStatus().getCurrentTask().getIcon() + "]");
        BotLogger.info("🎯", true,  bot.getId() + "Runtime Target Location: " + bot.getRuntimeStatus().getTargetLocation().toString() + " [ID: " + bot.getRuntimeStatus().getCurrentTask().getIcon() + "]");


        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();

        moveTaskParams.setTarget(target);
        moveTaskParams.setSpeed(multiplier);
        moveTask.setParams(moveTaskParams);
        
        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);

    }
}
