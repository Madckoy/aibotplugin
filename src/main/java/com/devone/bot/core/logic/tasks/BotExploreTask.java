package com.devone.bot.core.logic.tasks;

import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.BotTargetRandomizer;
import com.devone.bot.core.logic.navigation.filters.BotNavigablePointFilter;
import com.devone.bot.core.logic.navigation.filters.BotRemoveAirFilter;
import com.devone.bot.core.logic.navigation.filters.BotSolidBlockFilter;
import com.devone.bot.core.logic.navigation.filters.BotVerticalRangeFilter;
import com.devone.bot.core.logic.navigation.filters.BotWalkableSurfaceFilter;
import com.devone.bot.core.logic.navigation.resolvers.BotReachabilityResolver;
import com.devone.bot.core.logic.tasks.configs.BotExploreTaskConfig;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotNavigationUtils;
import com.devone.bot.utils.BotSceneData;
import com.devone.bot.utils.BotStringUtils;

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

        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Patrolling with radius: " + scanRadius + " [ID: " + uuid + "]");
        
        if(getSceneData()==null) {
            BotSonar3DTask sonar = new BotSonar3DTask(bot, this, scanRadius, scanRadius);
            bot.addTaskToQueue(sonar);
            return;
        }  

        BotSceneData sceneData = getSceneData();
        if (sceneData == null) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " No scene data available. [ID: " + uuid + "]");
            this.stop();
            return;
        }

        BotCoordinate3D bot_pos = bot.getRuntimeStatus().getCurrentLocation();

        List<BotBlockData> trimmed       = BotVerticalRangeFilter.filter(sceneData.blocks, bot_pos.y, 2);//relative!!!
        List<BotBlockData> solid         = BotSolidBlockFilter.filter(trimmed);
        List<BotBlockData> walkable      = BotWalkableSurfaceFilter.filter(solid);
        List<BotBlockData> navigable     = BotNavigablePointFilter.filter(BotRemoveAirFilter.filter(walkable));
        List<BotBlockData> reachable     = BotReachabilityResolver.resolve(bot_pos, navigable);
        List<BotBlockData> nav_targets   = BotNavigationPlannerWrapper.getNextExplorationTargets(bot_pos,reachable);

        BotCoordinate3D target = BotTargetRandomizer.pickRandomTarget(nav_targets);
        
        BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Target: " + BotStringUtils.formatLocation(target) + " [ID: " + uuid + "]");
        
        
        bot.getRuntimeStatus().setTargetLocation(target); 

        if (bot.getRuntimeStatus().getTargetLocation() == null) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Has finished exploration." +  " [ID: " + uuid + "]");
            this.stop();
            setSceneData(null);// reset scene map to force rescan
            return;
        }

        // ✅ Если бот уже идёт — не даём ему новую команду
        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Already moving, skipping exploration update."+ " [ID: " + uuid + "]");
        }

        double rand = Math.random();

        if (rand < 0.4) {
            // 📌 30% шанс выйти из патрулирования
            BotLogger.info(this.isLogged(), "🌐" + bot.getId() + " Moving out of exploration: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [Task ID: " + uuid + "]");
            bot.getRuntimeStatus().setTargetLocation(null);

            this.stop();

        } else {
            BotLogger.info(this.isLogged(), "🌐 " + bot.getId() + " Moving to exploration point: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [Task ID: " + uuid + "]");

            BotNavigationUtils.navigateTo(bot, bot.getRuntimeStatus().getTargetLocation()); // via a new MoVeTask()
        }

        setSceneData(null);
    }

    @Override
    public void stop() {
       this.isDone = true;
    }


}