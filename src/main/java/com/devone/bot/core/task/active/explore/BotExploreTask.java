package com.devone.bot.core.task.active.explore;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.BotNavigator.NavigationSuggestion;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.explore.params.BotExploreTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotExploreTask extends BotTaskAutoParams<BotExploreTaskParams> {

    private double scanRadius;
    private int rotations = 0;

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

        if (BotWorldHelper.isInDanger(bot)) {
            bot.getNavigator().calculate(sceneData, BotConstants.DEFAULT_MAX_SIGHT_FOV);
        } else {
            bot.getNavigator().calculate(sceneData, BotConstants.DEFAULT_NORMAL_SIGHT_FOV);
        }
        

        BotBlockData poi = bot.getNavigator().getSuggestedPoi();
        
        NavigationSuggestion suggestion = bot.getNavigator().getNavigationSuggestion();
        if(suggestion == NavigationSuggestion.CHANGE_DIRECTION) {
            if(rotations > 8) {
                BotLogger.debug(icon, isLogging(), bot.getId() + "  The full rotation was made and navigation point was not found. The bost is stuck!");                
                BotLogger.debug(icon, isLogging(), bot.getId() + "  It is up to BotBrain task to decide how to get the Bot unstuck");                

                bot.getNavigator().setStuck(true);

                stop();
                return;
            }
            BotLogger.debug(icon, isLogging(), bot.getId() + "  Rotating the bot to scan new sector!");
            //rotate 45 clockwise            
            BotUtils.rotateClockwise(this, bot, (float)BotConstants.DEFAULT_NORMAL_SIGHT_FOV);
            rotations++;
            return;
        }

        if (poi != null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🎯 Navigation - Set Target: " + poi);

            bot.getNavigator().setPoi(poi);

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
