package com.devone.bot.core.logic.tasks.explore;

import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.chat.BotChat;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.core.logic.navigation.selectors.BotBioSelector;
import com.devone.bot.core.logic.navigation.selectors.BotGeoSelector;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.explore.config.BotExploreTaskConfig;
import com.devone.bot.core.logic.tasks.explore.params.BotExploreTaskParams;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.core.logic.tasks.sonar.BotSonar3DTask;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.navigation.BotNavigationUtils;
import com.devone.bot.utils.scene.BotSceneData;

public class BotExploreTask extends BotTask {
  
    private int scanRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private BotExploreTaskConfig config;

    public BotExploreTask(Bot bot) {
        super(bot, "🌐");

        config = new BotExploreTaskConfig();

        this.isLogged = config.isLogged();
        this.scanRadius = config.getScanRadius();

        setObjective("Explore the area");

    }

    public void execute() {

        if (isPaused) return;

        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Exploring with radius: " + scanRadius + " [ID: " + uuid + "]");
        
        if(getSceneData()==null) {
            BotSonar3DTask sonar = new BotSonar3DTask(bot, this, scanRadius, scanRadius);
            bot.addTaskToQueue(sonar);
            return;
        }  
        
        setObjective("Exploring the area...");

        BotSceneData sceneData = getSceneData();
        if (sceneData == null) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " No scene data available. [ID: " + uuid + "]");
            this.stop();
            return;
        }

        BotCoordinate3D    bot_pos  = bot.getRuntimeStatus().getCurrentLocation();

        BotSceneContext context     = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, bot_pos);

        BotBlockData    navPoint  = BotGeoSelector.pickRandomTarget(context.blocks);

        BotBlockData    animal  = BotBioSelector.pickNearestTarget(context.entities, bot_pos);

        if (animal != null) {
            BotChat.broadcastMessage("I see an animal: " + animal.getCoordinate3D() + " [ID: " + uuid + "]");
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Spotted an animal. [ID: " + uuid + "]");
        }

        if (navPoint == null) {
            // 📌 Если цель не найдена, то выходим
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " No valid targets found. [ID: " + uuid + "]");
            this.stop();
            return;
        }

        // 📌 Если цель найдена, начинаем движение
        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Target: " + navPoint.getCoordinate3D() + " [ID: " + uuid + "]");
        
        bot.getRuntimeStatus().setTargetLocation(navPoint.getCoordinate3D()); 
        // 
        BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation()); // via a new MoVeTask()

        this.stop();
        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Exploration task completed. [ID: " + uuid + "]");
        return;
    }

    @Override
    public void stop() {
       this.isDone = true;
       setSceneData(null);
    }

    @Override
    public BotExploreTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams)params);
        
        if (params instanceof BotExploreTaskParams) {
            BotExploreTaskParams exploreParams = (BotExploreTaskParams) params;
            this.scanRadius = exploreParams.getScanRadius();
        } else {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Invalid parameters for `BotExploreTask`! [ID: " + uuid + "]");
            this.stop();
        }
        return this;
    }

}