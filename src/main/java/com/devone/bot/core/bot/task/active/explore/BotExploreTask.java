package com.devone.bot.core.bot.task.active.explore;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.navigation.BotNavigationUtils;
import com.devone.bot.core.bot.brain.logic.navigator.BotNavigationPlannerWrapper;
import com.devone.bot.core.bot.brain.logic.navigator.scene.BotSceneContext;
import com.devone.bot.core.bot.brain.logic.navigator.selectors.BotBlockSelector;
import com.devone.bot.core.bot.brain.memory.MemoryType;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.active.explore.params.BotExploreTaskParams;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
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
        setObjective(params.getObjective() + " ("+ rmt +")");

        if (rmt<=0) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Task timeout: " + getElapsedTime());
            this.stop();
            return;
        }

        if(params.isPickup()) {
            bot.pickupNearbyItems();
        }

        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();

        if (sceneData == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ No scene data available.");
            this.stop();
            return;
        }

        BotLocation botPos = bot.getNavigation().getLocation();

        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, botPos);

        boolean isStuck = BotNavigationUtils.detectIfStuck(bot);

        BotLogger.debug(icon, isLogging(), bot.getId() + " ❓ Stuck status: " + isStuck);


        BotBlockData target = BotBlockSelector.pickRandomTarget(context.reachableGoals);

        BotLogger.debug(icon, isLogging(), bot.getId() + " ❓ Total reachable points: " + context.reachableGoals.size());
        
        if (context.reachableGoals.size() <= 1) {
                // Бот застрял
                bot.getState().setStuck(true);
                BotLogger.debug(icon, this.isLogging(), bot.getId() + " ⛔ The bot is stuck!");
                stop();
                return;
            } else {
                // Фильтрация целей (без изменения оригинальной коллекции)
                List<BotBlockData> validGoals = new ArrayList<>();
                for (BotBlockData goal : context.reachableGoals) {
                if (!bot.getBrain().getMemory().isMemorized(goal, MemoryType.VISITED)) {
                  validGoals.add(goal);
                }
                if(validGoals.size()>0) {
                    target = validGoals.get(0);
                } else {
                    target = BotBlockSelector.pickEmergencyRelocationTarget(botPos, context.reachableGoals, context.reachable, context.navigable, context.walkable);
                }
            }
            
        }

        if (target != null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🎯 Target: " + target);
            bot.getNavigation().setTarget(target);
            BotNavigationUtils.navigateTo(bot, bot.getNavigation().getTarget(), 1);
            bot.getBrain().getMemory().memorize(target, MemoryType.VISITED); // Запоминаем на ~30 минут посещенную цель навигации

        } else {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⛔ No valid goal found.");
            bot.getState().setStuck(true);
            BotLogger.debug(getIcon(), isLogging(), bot.getId() + " ⛔ STUCK!! ");
            stop();
            return;
        }
    }


    @Override
    public void stop() {
        BotLogger.debug(icon, isLogging(), " ✅ Exploration task completed for " + bot.getId());
        super.stop();
    }

}
