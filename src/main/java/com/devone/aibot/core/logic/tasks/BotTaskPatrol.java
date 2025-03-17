package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskPatrolConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigation;
import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.Location;


public class BotTaskPatrol extends BotTask {
  
    private int patrolRadius = 10;
    private BotTaskPatrolConfig config;

    public BotTaskPatrol(Bot bot) {
        super(bot, "PATROL");
        this.config = new BotTaskPatrolConfig();
        this.patrolRadius = config.getPatrolRadius();
    }

    public void executeTask() {

        BotLogger.debug(bot.getId() + " 🚦 Состояние семафоров: "+ isDone + isPaused + BotStringUtils.formatLocation(targetLocation) + " [Task ID: " + taskId + "]");

        if (isPaused) return;

        if (shouldExitPatrol()) {
            BotLogger.debug("👀 " + bot.getId() + " Has finished patrolling." +  " [Task ID: " + taskId + "]");
            isDone = true; // ✅ Теперь `PATROL` корректно завершает себя
            return;
        }

        BotLogger.debug("👀 " + bot.getId() + " Patrolling with radius: " + patrolRadius + " [Task ID: " + taskId + "]");

        //Location newPatrolTarget;
        int attempts = 0;

        // 🛑 Не выбираем точку слишком близко!
        do {
            targetLocation = BotNavigation.getRandomPatrolPoint(bot, patrolRadius);
            attempts++;
        } while (targetLocation.distanceSquared(bot.getNPCEntity().getLocation()) < 4.0 && attempts < 5);

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.debug("👀 " + bot.getId() + " Already moving, skipping patrol update."+ " [Task ID: " + taskId + "]");
            return;
        }

        BotLogger.debug("🚶 " + bot.getId() + " Moving to patrol point: " + BotStringUtils.formatLocation(targetLocation) + " [Task ID: " + taskId + "]");
        BotNavigation.navigateTo(bot, targetLocation, 10);

        isDone = shouldExitPatrol();
    }

    private boolean shouldExitPatrol() {

        if (targetLocation == null) return true;

        if (BotNavigation.hasReachedTarget(bot, targetLocation, 2.0)) { // 🔧 Уменьшен tolerance, чтобы патруль не завершался сразу
            isDone = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone();
    }

    @Override
    public Location getTargetLocation() {
        return targetLocation;
    }
}