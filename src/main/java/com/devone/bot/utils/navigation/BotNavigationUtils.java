package com.devone.bot.utils.navigation;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.move.BotMoveTask;
import com.devone.bot.core.logic.tasks.move.params.BotMoveTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;


public class BotNavigationUtils {


    public static void navigateTo(Bot bot, BotCoordinate3D target) {
        navigate(bot, target, 2.5);
    }

    private static void navigate(Bot bot, BotCoordinate3D target, double multiplier) {

        
        BotLogger.info(true, bot.getId() + " 🏃🏻‍♂️‍➡️ Wants to navigate to " + target.toString() + " [ID: " + bot.getCurrentTask().getName() + "]");
        BotLogger.info(true, bot.getId() + " Runtime Target Location: " + bot.getRuntimeStatus().getTargetLocation().toString() + " [ID: " + bot.getCurrentTask().getName() + "]");


        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();
        moveTaskParams.setTarget(target);
        moveTaskParams.setSpeedMultiplier(multiplier);
        moveTask.configure(moveTaskParams);
        bot.addTaskToQueue(moveTask);
    }
}
