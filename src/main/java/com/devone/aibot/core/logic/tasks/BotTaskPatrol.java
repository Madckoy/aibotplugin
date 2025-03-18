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
  
    private int patrolRadius = 10;
    private BotTaskPatrolConfig config;

    public BotTaskPatrol(Bot bot) {
        super(bot, "🌊");
        this.config = new BotTaskPatrolConfig();
        this.patrolRadius = config.getPatrolRadius();
    }

    public void executeTask() {

        if (isPaused) return;

        BotLogger.debug("👀 " + bot.getId() + " Patrolling with radius: " + patrolRadius + " [ID: " + uuid + "]");

        targetLocation = BotNavigation.getRandomPatrolPoint(bot, patrolRadius);

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.debug("👀 " + bot.getId() + " Already moving, skipping patrol update."+ " [ID: " + uuid + "]");
            return;
        }

        if (targetLocation == null) {
            BotLogger.debug("👀 " + bot.getId() + " Has finished patrolling." +  " [ID: " + uuid + "]");
            isDone = true; // ✅ Теперь `PATROL` корректно завершает себя
            return;
        }

        double rand = Math.random();
        if (rand < 0.3) {
            // 📌 30% шанс выйти из патрулирования
            BotLogger.debug("🚶 " + bot.getId() + " Moving out of patroling: " + BotStringUtils.formatLocation(targetLocation) + " [Task ID: " + uuid + "]");
            targetLocation = null;
            isDone = true;
        } else {
            BotLogger.debug("🚶 " + bot.getId() + " Moving to patrol point: " + BotStringUtils.formatLocation(targetLocation) + " [Task ID: " + uuid + "]");

            BotNavigation.navigateTo(bot, targetLocation, patrolRadius); //via a new MoVeTask()
            isDone = false;
        }

    }

}