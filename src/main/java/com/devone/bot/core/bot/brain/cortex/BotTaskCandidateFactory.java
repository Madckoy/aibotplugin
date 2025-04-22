package com.devone.bot.core.bot.brain.cortex;

import com.devone.bot.core.bot.task.active.explore.BotExploreTask;
import java.util.*;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.navigator.math.selector.BotEntitySelector;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.bot.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.bot.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.bot.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.bot.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.bot.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotTaskCandidateFactory {

    public static List<BotTaskCandidate> createCandidates(Bot bot, BotBrainTaskParams params) {
        boolean isNight = BotWorldHelper.isNight(bot);
        BotSceneData data = bot.getBrain().getMemory().getSceneData();
        BotLocation botPos = bot.getNavigation().getLocation();

        List<BotTaskCandidate> candidates = new ArrayList<>();

        candidates.add(new BotTaskCandidate(
                () -> params.getViolenceWeight(),
                () -> {
                    BotBlockData target = BotEntitySelector.pickNearestTarget(data.entities, botPos, BotConstants.DEFAULT_DETECTION_RADIUS);
                    if (target == null)
                        return null;
                    return () -> {
                        BotLogger.debug("⚔️", bot.isLogging(), bot.getId() + " Атака на: " + target);
                        BotHandAttackTaskParams p = new BotHandAttackTaskParams(target, 5.0);
                        BotHandAttackTask t = new BotHandAttackTask(bot);
                        t.setParams(p);
                        BotTaskManager.push(bot, t);
                    };
                },
                () -> params.isAllowViolence() && BotEntitySelector.hasHostilesNearby(data.entities, botPos, BotConstants.DEFAULT_DETECTION_RADIUS)));

        candidates.add(new BotTaskCandidate(
                () -> params.getExplorationWeight(),
                () -> () -> {
                    BotLogger.debug("🧭", bot.isLogging(), bot.getId() + " Разведка");
                    BotTaskManager.push(bot, new BotExploreTask(bot));
                },
                () -> params.isAllowExploration()));

        candidates.add(new BotTaskCandidate(
                () -> params.getExcavationWeight(),
                () -> () -> {
                    BotLogger.debug("⛏", bot.isLogging(), bot.getId() + " Копка");
                    BotExcavateTask task = new BotExcavateTask(bot);
                    task.setParams(new BotExcavateTaskParams());
                    BotTaskManager.push(bot, task);
                },
                () -> params.isAllowExcavation()));

        return candidates;
    }
}
