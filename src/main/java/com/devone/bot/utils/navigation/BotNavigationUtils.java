package com.devone.bot.utils.navigation;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.move.BotMoveTask;
import com.devone.bot.core.logic.tasks.move.params.BotMoveTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;


public class BotNavigationUtils {


    public static void navigateTo(Bot bot, BotCoordinate3D target, float multiplier) {
        navigate(bot, target, multiplier);
    }

    private static void navigate(Bot bot, BotCoordinate3D target, float multiplier) {

        
        BotLogger.info(true, bot.getId() + " 🏃🏻‍♂️‍➡️ Wants to navigate to " + target.toString() + " [ID: " + bot.getRuntimeStatus().getCurrentTask().getName() + "]");
        BotLogger.info(true, bot.getId() + " Runtime Target Location: " + bot.getRuntimeStatus().getTargetLocation().toString() + " [ID: " + bot.getRuntimeStatus().getCurrentTask().getName() + "]");


        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();
        moveTaskParams.setTarget(target);
        moveTaskParams.setSpeedMultiplier(multiplier);
        moveTask.configure(moveTaskParams);
        bot.addTaskToQueue(moveTask);
    }
}
