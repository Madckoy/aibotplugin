package com.devone.bot.core.task.active.explore;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.BotNavigator.NavigationSuggestion;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.explore.params.BotExploreTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotExploreTask extends BotTaskAutoParams<BotExploreTaskParams> {

    private double scanRadius;

    public BotExploreTask(Bot bot) {
        super(bot, BotExploreTaskParams.class);
    }

    public IBotTaskParameterized<BotExploreTaskParams> setParams(BotExploreTaskParams params) {

        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());
        setEnabled(params.isEnabled());

        this.scanRadius = params.getScanRadius(); // Извлекаем параметр

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🧭 Explore with distance: " + scanRadius);

        if (isPause())
            return;
        

        long rmt = BotUtils.getRemainingTime(startTime, params.getTimeout());
        setObjective(params.getObjective() + " (" + rmt + ")");

        if (rmt <= 0) {
            this.stop();
            return;
        }

        if (params.isPickup()) {
            bot.pickupNearbyItems();
        }

        BotSceneData sceneData = bot.getBrain().getSceneData();

        if (sceneData == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ No scene data available.");
            this.stop();
            return;
        }

        BotBlockData target = bot.getNavigator().getSuggestedTarget();
        
        NavigationSuggestion suggestion = bot.getNavigator().getNavigationSuggestion();


        if (target != null) {
            if(suggestion == NavigationSuggestion.CHANGE_DIRECTION) {
                //rotate to the best YAW            
                BotUtils.rotate(this, bot, bot.getNavigator().getBestYaw());
            }

            BotLogger.debug(icon, isLogging(), bot.getId() + " 🎯 Navigation - Set Target: " + target);

            bot.getNavigator().setTarget(target);

            float speed = 1.5f;

            boolean canNavigate = bot.getNavigator().navigate(speed);
            
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❓ Navigation - Can navigate: " + canNavigate);

            // bot.getBrain().getMemory().memorize(target, MemoryType.VISITED_BLOCKS); //
            // Запоминаем на ~30 минут посещенную цель навигации
            stop();
            return;

        } else {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⛔ Navigation - No valid target found. Possibly stuck?");
            bot.getNavigator().setStuck(true);
            stop();
            return;
        }
    }

    @Override
    public void stop() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Exploration task completed");
        super.stop();
    }

}
