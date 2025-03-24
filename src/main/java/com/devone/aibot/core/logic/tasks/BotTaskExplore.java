package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskExploreConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtils;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotGeo3DScan;

public class BotTaskExplore extends BotTask {
  
    private int scanRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private BotTaskExploreConfig config;

    public BotTaskExplore(Bot bot) {
        super(bot, "🏞");
        config = new BotTaskExploreConfig();
        scanRadius = config.getScanRadius();
        setObjective("Explore the area");
    }

    public void executeTask() {

        if (isPaused) return;

        BotLogger.debug(isLogging(), "🏞 " + bot.getId() + " Patrolling with radius: " + scanRadius + " [ID: " + uuid + "]");
        
        if(getEnvMap()==null) {
            BotTaskSonar3D sonar = new BotTaskSonar3D(bot, this, scanRadius, 4);
            bot.addTaskToQueue(sonar);
            isDone = false;
            return;
        }  
    
        bot.getRuntimeStatus().setTargetLocation( BotGeo3DScan.getRandomEdgeBlock(getEnvMap())); 

        if (bot.getRuntimeStatus().getTargetLocation() == null) {
            BotLogger.debug(isLogging(), "🏞 " + bot.getId() + " Has finished exploration." +  " [ID: " + uuid + "]");
            isDone = true; // ✅ Теперь `PATROL` корректно завершает себя
            setEnvMap(null);// reset env map to force rescan
            return;
        }

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.debug(isLogging(), "🏞 " + bot.getId() + " Already moving, skipping exploration update."+ " [ID: " + uuid + "]");
            return;
        }

        double rand = Math.random();

        if (rand < 0.4) {
            // 📌 30% шанс выйти из патрулирования
            BotLogger.debug(isLogging(), "🏞 " + bot.getId() + " Moving out of exploration: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [Task ID: " + uuid + "]");
            bot.getRuntimeStatus().setTargetLocation(null);
            isDone = true;

        } else {
            BotLogger.debug(isLogging(), "🏞 " + bot.getId() + " Moving to exploration point: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [Task ID: " + uuid + "]");

            BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation()); // via a new MoVeTask()

            isDone = false;
        }

        setEnvMap(null);
    }

}