package com.devone.bot.core.bot.task.active.brain;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.behaviour.BotBehaviorSelector;
import com.devone.bot.core.bot.behaviour.BotTaskCandidate;
import com.devone.bot.core.bot.behaviour.BotTaskCandidatesFactory;
import com.devone.bot.core.bot.brain.logic.navigation.BotNavigationUtils;
import com.devone.bot.core.bot.brain.logic.navigator.BotNavigationPlannerWrapper;
import com.devone.bot.core.bot.brain.logic.navigator.scene.BotSceneContext;
import com.devone.bot.core.bot.brain.logic.navigator.selectors.BotBlockSelector;
import com.devone.bot.core.bot.brain.logic.navigator.selectors.BotEntitySelector;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.bot.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.bot.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.bot.task.active.idle.BotIdleTask;
import com.devone.bot.core.bot.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotBrainTask extends BotTaskAutoParams<BotBrainTaskParams> {

    public BotBrainTask(Bot bot) {
        super(bot, null, BotBrainTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotBrainTaskParams> setParams(BotBrainTaskParams params) {
        if (params == null) {
            throw new IllegalArgumentException("BotBrainTask: setParams(...) получил null");
        }

        super.setParams(params);
        setIcon(params.getIcon() != null ? params.getIcon() : "🧠");
        setObjective(params.getObjective() != null ? params.getObjective() : "Think");

        BotLogger.debug("🧠 BotBrainTask", isLogging(), bot.getId() + " параметры загружены: " +
                "explore=" + params.isAllowExploration() + ", " +
                "excavate=" + params.isAllowExcavation() + ", " +
                "violence=" + params.isAllowViolence() + ", " +
                "teleport=" + params.isAllowTeleport());

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🧠 Is making a decision...");

        int thinkingTicks = bot.getBrain().getThinkingTicks();
        if (thinkingTicks > 50) {
            BotLogger.warn(icon, isLogging(), bot.getId() + " 🧠 Бот думает слишком долго (" + thinkingTicks + " тиков). Сброс в Idle.");
            push(bot, new BotIdleTask(bot));
            bot.getBrain().resetThinkingCycle();
            return;
        }

        bot.getBrain().markThinkingCycle();

        long removed = bot.getBrain().getMemory().cleanup(params.getMemoryExpirationMillis());
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🗑️ Removed outdated navigation points: " + removed);

        BotSonar3DTask sonar = new BotSonar3DTask(bot);
        sonar.execute();

        if (bot.getBrain().getMemory().getSceneData() == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⛔ Ожидаем результаты сканирования...");
            return;
        }

        boolean isStuck = BotNavigationUtils.detectIfStuck(bot);
        bot.getState().setStuck(isStuck);

        Runnable decision = determineBehaviorScenario(bot);
        if (decision != null) decision.run();
    }

    private void push(Bot bot, BotTask<?> task) {

        BotUtils.pushTask(bot, task);
        
        bot.getBrain().resetThinkingCycle();

        BotLogger.debug(icon, isLogging(), "The task is pushed to stack ");
    }

    private Runnable determineBehaviorScenario(Bot bot) {
        boolean stuck = bot.getState().isStuck();

        if (stuck) {
            Optional<Runnable> unstuck = tryUnstuckStrategy(bot);
            if (unstuck.isPresent()) return unstuck.get();

            return () -> {
                BotLogger.debug(icon, isLogging(), bot.getId() + " 💤 Бот застрял. Уходим в Idle.");
                push(bot, new BotIdleTask(bot));
            };
        }

        Runnable weighted = pickWeightedTask(bot);
        return weighted != null ? weighted : () -> {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 💤 Нет задач. Уходим в Idle.");
            push(bot, new BotIdleTask(bot));
        };
    }

    private Runnable pickWeightedTask(Bot bot) {
        List<BotTaskCandidate> candidates = BotTaskCandidatesFactory.createCandidates(bot, params);
        Optional<Runnable> selected = BotBehaviorSelector.selectWeightedRandom(candidates);
        return selected.orElse(null);
    }

    private Optional<Runnable> tryUnstuckStrategy(Bot bot) {
        int strategy = params.getUnstuckStrategy();

        switch (strategy) {
            case 1:
                if (params.isAllowExcavation()) {
                    return Optional.of(() -> {
                        BotLogger.debug(icon, isLogging(), bot.getId() + " ⛏️ Копаемся чтобы выбраться");
                        BotExcavateTask task = new BotExcavateTask(bot);
                        BotExcavateTaskParams exParams = new BotExcavateTaskParams();
                        exParams.setOffsetY(exParams.getOuterRadius() - 1);
                        exParams.setPatternName("cone.yml");
                        task.setParams(exParams);
                        push(bot, task);
                    });
                }
                return Optional.empty();

            case 2:
                if (params.isAllowTeleport()) {
                    BotLocation botPos = bot.getNavigation().getLocation();
                    BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();
                    BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, botPos);
                    return tryTeleportFallback(bot, context);
                }
                return Optional.empty();

            default:
                return Optional.of(() -> {
                    BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Стратегия не определена. Idle.");
                    push(bot, new BotIdleTask(bot));
                });
        }
    }

    private Optional<Runnable> tryTeleportFallback(Bot bot, BotSceneContext context) {
        List<Supplier<Optional<Runnable>>> attempts = List.of(
            () -> tryTeleportToHostileEntity(bot, context.entities),
            () -> tryTeleportToReachable(bot, context.reachable),
            () -> tryTeleportToNavigable(bot, context.navigable),
            () -> tryTeleportToEntity(bot, context.entities),
            () -> tryTeleportToWalkable(bot, context.walkable),
            () -> tryTeleportToSpawn(bot)
        );

        for (Supplier<Optional<Runnable>> attempt : attempts) {
            Optional<Runnable> result = attempt.get();
            if (result.isPresent()) return result;
        }

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🚫 Все попытки телепортации провалились.");
        return Optional.empty();
    }

    private Optional<Runnable> tryTeleportToHostileEntity(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty()) return Optional.empty();

        List<BotBlockData> filtered = data.stream()
            .filter(target -> !BotWorldHelper.isInDangerousLiquid(target))
            .toList();

        if (filtered.isEmpty()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🌊 Все цели для телепортации находятся в жидкости.");
            return Optional.empty();
        }

        BotBlockData target = BotEntitySelector.pickNearestTarget(filtered, bot.getNavigation().getLocation(), BotConstants.DEFAULT_SCAN_RANGE);
        if (target == null) return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт на враждебную сущность вне жидкости: " + bot.getId() + " → " + target);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(target);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToReachable(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty()) return Optional.empty();
        BotBlockData block = BotBlockSelector.pickRandomTarget(data);
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт: достижимая точка " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToNavigable(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty()) return Optional.empty();
        BotBlockData block = BotBlockSelector.pickRandomTarget(data);
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт: точка навигации " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToEntity(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty()) return Optional.empty();
        BotBlockData block = BotBlockSelector.pickRandomTarget(data);
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт к сущности " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToWalkable(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty()) return Optional.empty();
        BotBlockData block = BotBlockSelector.pickRandomTarget(data);
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт: проходимая точка " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToSpawn(Bot bot) {
        BotLocation spawn = BotWorldHelper.getWorldSpawnLocation();
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт к спавну " + bot.getId() + " → " + spawn);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(spawn);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }
}