package com.devone.bot.utils;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.BotMoveTask;
import com.devone.bot.core.logic.tasks.params.BotMoveTaskParams;


public class BotNavigationUtils {
    
    public static boolean hasReachedTarget(BotCoordinate3D botLoc, BotCoordinate3D targetLoc) {

        return botLoc.equals(targetLoc);
    }

    public static void navigateTo(Bot bot, BotCoordinate3D target) {
        navigateTo(bot, target, 2.5);
    }

    public static void navigateTo(Bot bot, BotCoordinate3D target, double multiplier) {

        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();
        moveTaskParams.setTarget(target);
        moveTaskParams.setSpeedMultiplier(multiplier);
        moveTask.configure(moveTaskParams);
        bot.addTaskToQueue(moveTask);
    }
}
