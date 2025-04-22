package com.devone.bot.core.bot.task.active.explore;

import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.navigator.selectors.BotBlockSelector;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.active.explore.params.BotExploreTaskParams;
import com.devone.bot.core.bot.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotExploreTask extends BotTaskAutoParams<BotExploreTaskParams> {

    private int scanRadius;

    public BotExploreTask(Bot bot) {
        super(bot, BotExploreTaskParams.class);
    }

    public IBotTaskParameterized<BotExploreTaskParams> setParams(BotExploreTaskParams params) {

        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        this.scanRadius = params.getScanRadius(); // Извлекаем параметр

        return this;
    }

    @Override
    public void execute() {

        if (isPause())
            return;

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🧭 Explore with distance: " + scanRadius);

        long rmt = BotUtils.getRemainingTime(startTime);
        setObjective(params.getObjective() + " (" + rmt + ")");

        if (rmt <= 0) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Task timeout: " + getElapsedTime());
            this.stop();
            return;
        }

        if (params.isPickup()) {
            bot.pickupNearbyItems();
        }

        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();

        if (sceneData == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ No scene data available.");
            this.stop();
            return;
        }

        // BotLocation botPos = bot.getNavigation().getLocation();

        // BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(botPos,
        // sceneData.blocks, sceneData.entities);

        boolean isStuck = bot.getNavigation().isStuck();
        BotLogger.debug(icon, isLogging(), bot.getId() + " ❓ Stuck status: " + isStuck);

        List<BotBlockData> candidates = bot.getNavigation().getCandidates();

        BotBlockData target = BotBlockSelector.pickRandomTarget(candidates);

        BotLogger.debug(icon, isLogging(), bot.getId() + " ❓ Total reachable points: " + candidates.size());



        if (target != null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🎯 Target: " + target);

            bot.getNavigation().setTarget(target);

            float speed = 1.5f;

            boolean canNavigate = bot.getNavigation().navigate(speed);

            
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❓ Navigation result:" + canNavigate);

            // bot.getBrain().getMemory().memorize(target, MemoryType.VISITED_BLOCKS); //
            // Запоминаем на ~30 минут посещенную цель навигации

        } else {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⛔ No valid target found. Possibly stuck?");
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
