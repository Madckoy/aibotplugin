package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskPatrolConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtils;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotEnv3DScan;

public class BotTaskPatrol extends BotTask {
  
    private int scanRadius = 10;
    private BotTaskPatrolConfig config;

    public BotTaskPatrol(Bot bot) {
        super(bot, "👮🏻‍♂️");
        this.config = new BotTaskPatrolConfig();
        this.scanRadius = config.getScanRadius();
    }

    public void executeTask() {

        if (isPaused) return;

        BotLogger.debug("👮🏻‍♂️ " + bot.getId() + " Patrolling with radius: " + scanRadius + " [ID: " + uuid + "]");
        
        if(getEnvMap()==null) {
            BotTaskSonar3D sonar = new BotTaskSonar3D(bot, this);
            bot.addTaskToQueue(sonar);
            isDone = false;
            return;
        }  

        if (targetLocation == null) {
            
            targetLocation = BotEnv3DScan.getRandomEdgeBlock(getEnvMap());

            Bot.navigateTo(bot, targetLocation);
        } 

        if (targetLocation == null) {
            BotLogger.debug("👮🏻‍♂️ " + bot.getId() + " Has finished patrolling." +  " [ID: " + uuid + "]");
            isDone = true; // ✅ Теперь `PATROL` корректно завершает себя
            setEnvMap(null);// reset env map to force rescan
            return;
        }

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.debug("👮🏻‍♂️ " + bot.getId() + " Already moving, skipping patrol update."+ " [ID: " + uuid + "]");
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

            Bot.navigateTo(bot, targetLocation); // via a new MoVeTask()

            isDone = false;
        }

    }

}