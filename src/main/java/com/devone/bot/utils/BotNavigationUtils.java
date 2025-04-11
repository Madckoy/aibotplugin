package com.devone.bot.utils;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.BotMoveTask;
import com.devone.bot.core.logic.tasks.params.BotMoveTaskParams;


public class BotNavigationUtils {
    
    public static boolean hasReachedTarget(BotCoordinate3D botLoc, BotCoordinate3D targetLoc) {
        BotLogger.info(true, "botLoc:"+botLoc.toString());
        BotLogger.info(true, "targetLoc:"+targetLoc.toString());
        return botLoc.equals(targetLoc);
    }

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
