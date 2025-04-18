package com.devone.bot.core.brain.behaviour;

import java.util.*;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.explore.BotExploreTask;
import com.devone.bot.core.logic.task.excavate.BotExcavateTask;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.logic.task.hand.attack.BotHandAttackTask;
import com.devone.bot.core.logic.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.core.logic.navigation.selectors.BotEntitySelector;
import com.devone.bot.core.logic.task.brain.params.BotBrainTaskParams;
import com.devone.bot.utils.scene.BotSceneData;
import com.devone.bot.utils.world.BotWorldHelper;
import com.devone.bot.utils.logger.BotLogger;

public class BotTaskCandidatesFactory {

    public static List<BotTaskCandidate> createCandidates(Bot bot, BotBrainTaskParams params) {
        boolean isNight = BotWorldHelper.isNight(bot);
        BotSceneData data = bot.getBrain().getMemory().getSceneData();
        BotLocation botPos = bot.getBrain().getCurrentLocation();

        List<BotTaskCandidate> candidates = new ArrayList<>();

        candidates.add(new BotTaskCandidate(
            () -> isNight ? params.getViolenceWeight() : params.getViolenceWeight() * 0.3,
            () -> {
                BotBlockData target = BotEntitySelector.pickNearestTarget(data.entities, botPos, 2.0);
                if (target == null) return null;
                return () -> {
                    BotLogger.info("⚔️", bot.isLogging(), bot.getId() + " Атака на: " + target);
                    BotHandAttackTaskParams p = new BotHandAttackTaskParams(target, 5.0);
                    BotHandAttackTask t = new BotHandAttackTask(bot);
                    t.setParams(p);
                    bot.getLifeCycle().getTaskStackManager().pushTask(t);
                };
            },
            () -> params.isAllowViolence() && BotEntitySelector.hasHostilesNearby(data.entities, botPos, 2.0)
        ));

        candidates.add(new BotTaskCandidate(
            () -> isNight ? params.getExplorationWeight() * 0.6 : params.getExplorationWeight(),
            () -> () -> {
                BotLogger.info("🧭", bot.isLogging(), bot.getId() + " Разведка");
                bot.getLifeCycle().getTaskStackManager().pushTask(new BotExploreTask(bot));
            },
            () -> params.isAllowExploration()
        ));

        candidates.add(new BotTaskCandidate(
            () -> isNight ? params.getExcavationWeight() * 0.5 : params.getExcavationWeight(),
            () -> () -> {
                BotLogger.info("⛏", bot.isLogging(), bot.getId() + " Копка");
                BotExcavateTask task = new BotExcavateTask(bot);
                task.setParams(new BotExcavateTaskParams());
                bot.getLifeCycle().getTaskStackManager().pushTask(task);
            },
            () -> params.isAllowExcavation()
        ));

        return candidates;
    }
}
