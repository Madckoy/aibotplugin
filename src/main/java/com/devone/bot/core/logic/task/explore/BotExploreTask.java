package com.devone.bot.core.logic.task.explore;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.core.logic.navigation.selectors.BotBioSelector;
import com.devone.bot.core.logic.navigation.selectors.BotGeoSelector;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.attack.survival.BotSurvivalAttackTask;
import com.devone.bot.core.logic.task.attack.survival.params.BotSurvivalAttackTaskParams;
import com.devone.bot.core.logic.task.explore.config.BotExploreTaskConfig;
import com.devone.bot.core.logic.task.explore.params.BotExploreTaskParams;
import com.devone.bot.core.logic.task.hand.attack.BotHandAttackTask;
import com.devone.bot.core.logic.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.core.logic.task.sonar.BotSonar3DTask;
import com.devone.bot.core.logic.task.teleport.BotTeleportTask;
import com.devone.bot.core.logic.task.teleport.params.BotTeleportTaskParams;
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

        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Exploring with radius: " + scanRadius);
        
        if(getSceneData()==null) {
            BotSonar3DTask sonar = new BotSonar3DTask(bot, this, scanRadius, scanRadius);
            bot.addTaskToQueue(sonar);
            return;
        }  
        
        setObjective("Exploring the area...");

        if(bot.getRuntimeStatus().isStuck()) { // force sonar scan
            BotSonar3DTask sonar = new BotSonar3DTask(bot, this, scanRadius, scanRadius);
            bot.addTaskToQueue(sonar);
        }

        BotSceneData sceneData = getSceneData();
        if (sceneData == null) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " No scene data available.");
            this.stop();
            return;
        }

        BotCoordinate3D    bot_pos  = bot.getRuntimeStatus().getCurrentLocation();

        BotSceneContext context     = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, bot_pos);

        BotBlockData    goal    = BotGeoSelector.pickRandomTarget(context.reachableGoals);

        BotBlockData    animal      = BotBioSelector.pickNearestTarget(context.entities, bot_pos);

        //Block block = BotWorldHelper.getBlockAt(navTarget.getCoordinate3D());

        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Total nav targets: " + context.reachableGoals);


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
            if(animal!=null) {
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Inflicting Survival Strike to unstuck!");
                BotSurvivalAttackTaskParams params = new BotSurvivalAttackTaskParams(animal, 5.0);
                BotSurvivalAttackTask strikeTask = new BotSurvivalAttackTask(bot).configure(params);
                bot.addTaskToQueue(strikeTask);
                stop();
                return;
            } else {
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " No animal found to unstuck.");
                //----------
                if(getElapsedTime() > BotConstants.DEFAULT_TASK_TIMEOUT) {
                    BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Task timeout.");
        
                    BotBlockData fallback = BotGeoSelector.pickEmergencyTeleportTarget(bot.getRuntimeStatus().getCurrentLocation(), 
                                                                                       context.reachableGoals, 
                                                                                       context.reachable, 
                                                                                       context.navigable, 
                                                                                       context.walkable);
        
                    if (fallback != null) {
                        BotLogger.warn(true, bot.getId() + " 🌀 Навигация невозможна, но есть путь — телепорт к: " + fallback);
        
                        BotTeleportTaskParams tpParams = new BotTeleportTaskParams(fallback.getCoordinate3D());
                        BotTeleportTask tpTask = new BotTeleportTask(bot, null).configure(tpParams);
                        bot.addTaskToQueue(tpTask);
                        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Teleporting to fallback location: " + fallback.getCoordinate3D());
                        
                        this.stop();
                        return;
                    }
                }
                return;
            }
        } 
   
        if(animal != null) {
                BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Inflicting Attack to bring justice on: "+animal);
                BotHandAttackTaskParams handParams = new BotHandAttackTaskParams(animal, 5.0);
                BotHandAttackTask handTask = new BotHandAttackTask(bot).configure(handParams);
                bot.addTaskToQueue(handTask);
                return;
        }

        
        // 📌 Если цель найдена, начинаем движение
        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Target: " + goal.getCoordinate3D());
        //
        bot.getRuntimeStatus().setTargetLocation(goal.getCoordinate3D()); 
        //
        BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation(), 1); // via a new MoVeTask()
        //
        if(getElapsedTime() > 3 * BotConstants.DEFAULT_TASK_TIMEOUT) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " "+ this.name +" Task timeout: "+getElapsedTime());
            this.stop();
        }
        return;
    }

    @Override
    public void stop() {
       this.isDone = true;
       setSceneData(null);
       BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Exploration task completed.");
    }

    @Override
    public BotExploreTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams)params);
        
        if (params instanceof BotExploreTaskParams) {
            BotExploreTaskParams exploreParams = (BotExploreTaskParams) params;
            this.scanRadius = exploreParams.getScanRadius();
        } else {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Invalid parameters for `BotExploreTask`!");
            this.stop();
        }
        return this;
    }

}