package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotExploreTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtils;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.Bot3DGeoScan;

public class BotExploreTask extends BotTask {
  
    private int scanRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private BotExploreTaskConfig config;

    public BotExploreTask(Bot bot) {
        super(bot, "🏞️");

        config = new BotExploreTaskConfig();
        this.isLogged = config.isLogged();
        
        this.scanRadius = config.getScanRadius();

        setObjective("Explore the area");

    }

    public void executeTask() {

        if (isPaused) return;

        BotLogger.info(this.isLogged(), "🏞 " + bot.getId() + " Patrolling with radius: " + scanRadius + " [ID: " + uuid + "]");
        
        if(getEnvMap()==null) {
            BotSonar3DTask sonar = new BotSonar3DTask(bot, this, scanRadius, 4);
            bot.addTaskToQueue(sonar);
            isDone = false;
            return;
        }  
    
        bot.getRuntimeStatus().setTargetLocation( Bot3DGeoScan.getRandomEdgeBlock(getEnvMap())); 

        if (bot.getRuntimeStatus().getTargetLocation() == null) {
            BotLogger.info(this.isLogged(), "🏞 " + bot.getId() + " Has finished exploration." +  " [ID: " + uuid + "]");
            isDone = true; // ✅ Теперь `PATROL` корректно завершает себя
            setEnvMap(null);// reset env map to force rescan
            return;
        }

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.info(this.isLogged(), "🏞 " + bot.getId() + " Already moving, skipping exploration update."+ " [ID: " + uuid + "]");
            return;
        }

        double rand = Math.random();

        if (rand < 0.4) {
            // 📌 30% шанс выйти из патрулирования
            BotLogger.info(this.isLogged(), "🏞 " + bot.getId() + " Moving out of exploration: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [Task ID: " + uuid + "]");
            bot.getRuntimeStatus().setTargetLocation(null);
            isDone = true;

        } else {
            BotLogger.info(this.isLogged(), "🏞 " + bot.getId() + " Moving to exploration point: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [Task ID: " + uuid + "]");

            BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation()); // via a new MoVeTask()

            isDone = false;
        }

        setEnvMap(null);
    }


}