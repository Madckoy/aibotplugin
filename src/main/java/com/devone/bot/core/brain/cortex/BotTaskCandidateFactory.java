package com.devone.bot.core.brain.cortex;

import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.active.explore.BotExploreTask;
import java.util.*;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTaskCandidateFactory {

    public static List<BotTaskCandidate> createCandidates(Bot bot, BotBrainTaskParams params) {
        BotSceneData data = bot.getBrain().getSceneData();
        BotPosition botPos = bot.getNavigator().getPosition();

        List<BotTaskCandidate> candidates = new ArrayList<>();

        candidates.add(new BotTaskCandidate(
                () -> getViolenceWeight(),
                () -> {
                    BotBlockData target = BotEntitySelector.pickNearestTarget(data.entities, botPos, BotConstants.DEFAULT_DETECTION_RADIUS);
                    if (target == null)
                        return null;
                    return () -> {
                        BotLogger.debug("⚔️", bot.isLogged(), bot.getId() + " Атака на: " + target);
                        BotHandAttackTaskParams p = new BotHandAttackTaskParams(target, 5.0);
                        BotHandAttackTask t = new BotHandAttackTask(bot);
                        t.setParams(p);
                        BotTaskManager.push(bot, t);
                    };
                },
                () -> isAllowViolence() && BotEntitySelector.hasHostilesNearby(data.entities, botPos, BotConstants.DEFAULT_DETECTION_RADIUS)));

        candidates.add(new BotTaskCandidate(
                () -> getExplorationWeight(),
                () -> () -> {
                    BotLogger.debug("🧭", bot.isLogged(), bot.getId() + " Разведка");
                    BotTaskManager.push(bot, new BotExploreTask(bot));
                },
                () -> isAllowExploration()));

        candidates.add(new BotTaskCandidate(
                () -> getExcavationWeight(),
                () -> () -> {
                    BotLogger.debug("⛏", bot.isLogged(), bot.getId() + " Копка");
                    BotExcavateTask task = new BotExcavateTask(bot);
                    task.setParams(task.getParams());
                    BotTaskManager.push(bot, task);
                },
                () -> isAllowExcavation()));

        return candidates;
    }

    public static boolean isAllowViolence(){
        return true;
    }

    public static boolean isAllowExploration(){
        return true;
    }

    public static boolean isAllowExcavation(){
        return true;
    }

    public static double getViolenceWeight(){
        return 0.5;
    }
    public static double getExplorationWeight(){
        return 0.5;
    }

    public static double getExcavationWeight(){
        return 0.1;
    }
}
