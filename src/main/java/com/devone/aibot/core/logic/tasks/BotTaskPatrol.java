package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskPatrolConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigation;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.EnvironmentScanner;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;


public class BotTaskPatrol extends BotTask {
  
    private int patrolRadius = 15;
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

        targetLocation = BotNavigation.getRandomPatrolPoint(bot, patrolRadius);

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

        BotNavigation.navigateTo(bot, targetLocation, 15); //via a new MoVeTask()

        this.isDone = false;

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