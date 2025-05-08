package com.devone.bot.core.task.active.brain;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSelector;
import com.devone.bot.core.brain.cortex.BotTaskCandidate;
import com.devone.bot.core.brain.cortex.BotTaskCandidateFactory;

import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContextMaker;

import com.devone.bot.core.brain.logic.navigator.math.selector.BotEntitySelector;
import com.devone.bot.core.brain.logic.navigator.math.selector.BotPOISelector;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;
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

        bot.getBrain().setMemoryExpirationMillis(params.getMemoryExpirationMillis());

        BotLogger.debug(icon, isLogging(), bot.getId() + " ⚙️ Параметры загружены: " +
                "explore=" + params.isAllowExploration() + ", " +
                "excavate=" + params.isAllowExcavation() + ", " +
                "violence=" + params.isAllowViolence() + ", " +
                "teleport=" + params.isAllowTeleport());

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🎲 Is making a decision...");

        int thinkingTicks = bot.getBrain().getThinkingTicks();
        
        if (thinkingTicks > 50) {
            BotLogger.warn(icon, isLogging(),
                    bot.getId() + " 🎲 Бот думает слишком долго (" + thinkingTicks + " тиков). Сброс в Calibration.");
            push(bot, new BotCalibrateTask(bot));
            bot.getBrain().resetThinkingCycle();
            return;
        }

        bot.getBrain().markThinkingCycle();

        boolean stuck = bot.getNavigator().isStuck();
        
        if(stuck) {
            bot.getNavigator().calculate(bot.getBrain().getSceneData(), BotConstants.DEFAULT_MAX_SIGHT_FOV); // ищем все возможные варианты POI и пробуем self-unstuck
            return;
        }

        Runnable decision = determineBehaviorScenario(bot);
        if (decision != null)
            decision.run();
    }

    private void push(Bot bot, BotTask<?> task) {

        BotTaskManager.push(bot, task);

        bot.getBrain().resetThinkingCycle();

        BotLogger.debug(icon, isLogging(), "The task is pushed to stack ");
    }

    private Runnable determineBehaviorScenario(Bot bot) {

        boolean stuck = bot.getNavigator().isStuck();

        if (stuck) {

            Optional<Runnable> unstuck = tryUnstuckStrategy(bot);
            if (unstuck.isPresent())
                return unstuck.get();

            return () -> {
                BotLogger.debug(icon, isLogging(), bot.getId() + " 💤 Бот застрял. Уходим в Calibration.");
                push(bot, new BotCalibrateTask(bot));
            };
        }

        Runnable weighted = pickWeightedTask(bot);

        return weighted != null ? weighted : () -> {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 💤 Нет задач. Уходим в Calibration.");
            push(bot, new BotCalibrateTask(bot));
        };
    }

    private Runnable pickWeightedTask(Bot bot) {
        List<BotTaskCandidate> candidates = BotTaskCandidateFactory.createCandidates(bot, params);
        Optional<Runnable> selected = BotActionSelector.selectWeightedRandom(candidates);
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
                        task.setParams(exParams);
                        push(bot, task);
                    });
                }
                return Optional.empty();

            case 2:
                if (params.isAllowTeleport()) {
                    BotPositionSight botPos = bot.getNavigator().getPositionSight();
                    BotSceneData sceneData = bot.getBrain().getSceneData();
                    BotNavigationContext context = BotNavigationContextMaker.createSceneContext(botPos, sceneData.blocks,
                            sceneData.entities, BotConstants.DEFAULT_MAX_SIGHT_FOV);
                    return tryTeleportFallback(bot, context);
                }
                return Optional.empty();

            default:
                return Optional.of(() -> {
                    BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Стратегия не определена. Calibrate.");
                    push(bot, new BotCalibrateTask(bot));
                });
        }
    }

    private Optional<Runnable> tryTeleportFallback(Bot bot, BotNavigationContext context) {
        List<Supplier<Optional<Runnable>>> attempts = List.of(
                () -> tryTeleportToHostileEntity(bot, context.entities),
                () -> tryTeleportToReachable(bot, context.reachable),
                () -> tryTeleportToNavigable(bot, context.navigable),
                () -> tryTeleportToEntity(bot, context.entities),
                () -> tryTeleportToWalkable(bot, context.walkable),
                () -> tryTeleportToSpawn(bot));

        for (Supplier<Optional<Runnable>> attempt : attempts) {
            Optional<Runnable> result = attempt.get();
            if (result.isPresent())
                return result;
        }

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🚫 Все попытки телепортации провалились.");
        return Optional.empty();
    }

    private Optional<Runnable> tryTeleportToHostileEntity(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty())
            return Optional.empty();

        List<BotBlockData> filtered = data.stream()
                .filter(target -> !BotWorldHelper.isInDangerousLiquid(target))
                .toList();

        if (filtered.isEmpty()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🌊 Все цели для телепортации находятся в жидкости.");
            return Optional.empty();
        }

        BotBlockData target = BotEntitySelector.pickNearestTarget(filtered, bot.getNavigator().getPosition(),
                BotConstants.DEFAULT_SCAN_RANGE);
        if (target == null)
            return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(),
                    "⚡Телепорт на враждебную сущность вне жидкости: " + bot.getId() + " → " + target);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(target.getPosition());
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToReachable(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty())
            return Optional.empty();
            BotPosition block = BotPOISelector.selectRandom( BlockUtils.fromBlocks(data));
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт: достижимая точка " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToNavigable(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty())
            return Optional.empty();
            BotPosition block = BotPOISelector.selectRandom( BlockUtils.fromBlocks(data));
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт: точка навигации " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToEntity(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty())
            return Optional.empty();
            BotPosition block = BotPOISelector.selectRandom(BlockUtils.fromBlocks(data));
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт к сущности " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToWalkable(Bot bot, List<BotBlockData> data) {
        if (data == null || data.isEmpty())
            return Optional.empty();
            BotPosition block = BotPOISelector.selectRandom(BlockUtils.fromBlocks(data));
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт: проходимая точка " + bot.getId() + " → " + block);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToSpawn(Bot bot) {
        BotPosition spawn = BotWorldHelper.getWorldSpawnLocation();
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), "⚡Телепорт к спавну " + bot.getId() + " → " + spawn);
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(spawn);
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
            push(bot, tpTask);
        });
    }
}