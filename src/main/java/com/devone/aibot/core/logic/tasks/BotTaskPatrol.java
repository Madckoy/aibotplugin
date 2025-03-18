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

        BotLogger.debug("🚦 " + bot.getId() + " " + this.name +" Status: "+ this.isDone +" | " +this.isPaused +
        " 📍 xyz: " +BotStringUtils.formatLocation(bot.getNPCCurrentLocation())+
        " 🎯 xyz: " +BotStringUtils.formatLocation(this.targetLocation) + " [ID: " + this.uuid + "]");

        if (this.isPaused) return;

        BotLogger.debug("👀 " + bot.getId() + " Patrolling with radius: " + patrolRadius + " [ID: " + uuid + "]");

        //Location newPatrolTarget;
        int attempts = 0;

        // 🛑 Не выбираем точку слишком близко!
        do {
            this.targetLocation = BotNavigation.getRandomPatrolPoint(bot, patrolRadius);
            attempts++;
        } while (this.targetLocation.distanceSquared(bot.getNPCEntity().getLocation()) < 4.0 && attempts < 5);

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.debug("👀 " + bot.getId() + " Already moving, skipping patrol update."+ " [ID: " + uuid + "]");
            return;
        }

        if (shouldExitPatrol()) {
            BotLogger.debug("👀 " + bot.getId() + " Has finished patrolling." +  " [ID: " + uuid + "]");
            isDone = true; // ✅ Теперь `PATROL` корректно завершает себя
            return;
        }

        

        BotLogger.debug("🚶 " + bot.getId() + " Moving to patrol point: " + BotStringUtils.formatLocation(this.targetLocation) + " [Task ID: " + uuid + "]");

        //BotNavigation.navigateTo(bot, this.targetLocation, 10); //via a new MoVeTask()

        this.isDone = true;

    }

    private boolean shouldExitPatrol() {

        if (this.targetLocation == null) return true;

        if (BotNavigation.hasReachedTarget(bot, this.targetLocation, 2.0)) { // 🔧 Уменьшен tolerance, чтобы патруль не завершался сразу
            this.isDone = true;
            return true;
        }
        return false;
    }
}