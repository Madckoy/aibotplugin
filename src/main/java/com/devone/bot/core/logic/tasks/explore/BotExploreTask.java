package com.devone.bot.core.logic.tasks.explore;

import com.devone.bot.core.logic.tasks.strikes.params.BotSurvivalStrikeTaskParams;
import com.devone.bot.core.logic.tasks.teleport.BotTeleportTask;
import com.devone.bot.core.logic.tasks.teleport.params.BotTeleportTaskParams;
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
import com.devone.bot.core.logic.tasks.strikes.BotSurvivalStrikeTask;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.navigation.BotNavigationUtils;
import com.devone.bot.utils.scene.BotSceneData;
import com.devone.bot.utils.world.BotWorldHelper;
import org.bukkit.block.Block;

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

        BotBlockData    navTarget    = BotGeoSelector.pickRandomTarget(context.navTargets);

        BotBlockData    animal      = BotBioSelector.pickNearestTarget(context.entities, bot_pos);

        Block block = BotWorldHelper.getBlockAt(navTarget.getCoordinate3D());

        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Total nav targets: " + context.navTargets);


        //if(bot.getNPCEntity() != null) {
        //    if(bot.getNPCNavigator().canNavigateTo(block.getLocation())==false) {
        //        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Navigation to target is not possible. [ID: " + uuid + "]");
        //        bot.getRuntimeStatus().setStuck(true);
        //    }
        //} else {
        //    BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " NPC entity is null. [ID: " + uuid + "]");
        //    this.stop();
        //    return;
        //}

        if(bot.getRuntimeStatus().isStuck()) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Bot is stuck. [ID: " + uuid + "]");
            if(animal != null) {
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Inflicting survival strike to unstuck!  [ID: " + uuid + "]");
                BotSurvivalStrikeTaskParams params = new BotSurvivalStrikeTaskParams(animal, 5.0);
                BotSurvivalStrikeTask strikeTask = new BotSurvivalStrikeTask(bot).configure(params);
                bot.addTaskToQueue(strikeTask);
                stop();
                return;
            } else {
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " No animal found to unstuck. [ID: " + uuid + "]");
                this.stop();
                return;
            }
        } else {
            if(animal != null) {
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Inflicting justified strike!  [ID: " + uuid + "]");
                BotSurvivalStrikeTaskParams params = new BotSurvivalStrikeTaskParams(animal, 5.0);
                BotSurvivalStrikeTask strikeTask = new BotSurvivalStrikeTask(bot).configure(params);
                bot.addTaskToQueue(strikeTask);
                stop();
                return;
            }
        }

        // 📌 Если цель найдена, начинаем движение
        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Target: " + navTarget.getCoordinate3D() + " [ID: " + uuid + "]");
        //
        bot.getRuntimeStatus().setTargetLocation(navTarget.getCoordinate3D()); 
        //
        BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation()); // via a new MoVeTask()
        //
        if(currentTimeMillis() - startTime > BotConstants.DEFAULT_TASK_TIMEOUT) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Task timeout. [ID: " + uuid + "]");

            BotBlockData fallback = BotGeoSelector.pickEmergencyTeleportTarget(bot.getRuntimeStatus().getCurrentLocation(), context.navTargets);

            if (fallback != null) {
                BotLogger.warn(true, bot.getId() + " 🌀 Навигация невозможна, но есть путь — телепорт к: " + fallback);

                BotTeleportTaskParams tpParams = new BotTeleportTaskParams(fallback.getCoordinate3D());
                BotTeleportTask tpTask = new BotTeleportTask(bot, null).configure(tpParams);
                bot.addTaskToQueue(tpTask);
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Teleporting to fallback location: " + fallback.getCoordinate3D() + " [ID: " + uuid + "]");
                this.stop();
                return;
            }
        }
        return;
    }

    @Override
    public void stop() {
       this.isDone = true;
       setSceneData(null);
       BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Exploration task completed. [ID: " + uuid + "]");
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