package com.devone.bot.core.task.active.brain;

import java.util.Comparator;
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
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.active.explore.BotExploreTask;
import com.devone.bot.core.task.active.swim.BotSwimTask;
import com.devone.bot.core.task.active.swim.params.BotSwimTaskParams;
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

            push(bot, new BotCalibrateTask(bot,"Need reset!"));

            bot.getBrain().resetThinkingCycle();
            return;
        }

        bot.getBrain().markThinkingCycle();

        boolean stuck = bot.getNavigator().isStuck();
        

        int radius = BotConstants.DEFAULT_SCAN_RANGE;

        Integer scanRadius = (Integer) BotMemoryV2Utils.readMemoryValue(bot, "navigation", "scanRadius");
            
        if(scanRadius!=null) {
            radius = scanRadius.intValue();
        }

        if(stuck) {

            radius++;

            BotMemoryV2Utils.memorizeValue(bot, "navigation", "scanRadius", radius);

            bot.getNavigator().calculate(bot.getBrain().getSceneData(), BotConstants.DEFAULT_MAX_SIGHT_FOV); // ищем все возможные варианты targets и пробуем self-unstuck

        } else {

            BotMemoryV2Utils.memorizeValue(bot, "navigation", "scanRadius", BotConstants.DEFAULT_SCAN_RANGE);

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

        BotPositionSight botPos = bot.getNavigator().getPositionSight();                    
        BotSceneData sceneData = bot.getBrain().getSceneData();
        BotNavigationContext context = BotNavigationContextMaker.createSceneContext(botPos, sceneData.blocks,
        sceneData.entities, BotConstants.DEFAULT_MAX_SIGHT_FOV);

        if (stuck) {

            Optional<Runnable> unstuck = tryUnstuckStrategy(bot, context);

            if (unstuck.isPresent()) {
             
                return unstuck.get();
            }

            return () -> {
                BotLogger.debug(icon, isLogging(), bot.getId() + " 💤 Бот застрял. Уходим в Calibration с сообщением. ");

                push(bot, new BotCalibrateTask(bot, "I'm stuck. Need help!"));
            };
        }

        Runnable weighted = pickWeightedTask(bot);

        return weighted != null ? weighted : () -> {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 💤 Нет задач. Уходим в Calibration.");
            push(bot, new BotCalibrateTask(bot, "Ready"));
        };
    }

    private Runnable pickWeightedTask(Bot bot) {
        List<BotTaskCandidate> candidates = BotTaskCandidateFactory.createCandidates(bot, params);
        Optional<Runnable> selected = BotActionSelector.selectWeightedRandom(candidates);
        return selected.orElse(null);
    }

    private Optional<Runnable> tryUnstuckStrategy(Bot bot, BotNavigationContext context) {

        int strategy = params.getUnstuckStrategy();

        switch (strategy) {
            case 0:
                if (params.isAllowSwimming()) {
                Optional<Runnable> swim = trySwimToLand(bot, context);
                if (swim.isPresent()) return swim;
                    params.setUnstuckStrategy(1);
                } else {
                    params.setUnstuckStrategy(1);
                }
                return tryUnstuckStrategy(bot, context);
            case 1:
                if (params.isAllowExploration()) {
                    return Optional.of(() -> {
                        BotLogger.debug(icon, isLogging(), bot.getId() + " 🚶🏻‍♂️Двигаемся чтобы выбраться");
                        BotExploreTask task = new BotExploreTask(bot);
                        push(bot, task);
                        params.setUnstuckStrategy(2);
                    });
                }
                return Optional.empty();
            case 2:
                if (params.isAllowExcavation()) {
                    return Optional.of(() -> {
                        BotLogger.debug(icon, isLogging(), bot.getId() + " ⛏️ Копаемся чтобы выбраться");
                        BotExcavateTask task = new BotExcavateTask(bot);
                        BotExcavateTaskParams exParams = new BotExcavateTaskParams();
                        exParams.setPatternName("escape.json");
                        task.setParams(exParams);
                        push(bot, task);
                        params.setUnstuckStrategy(3);
                    });
                }
                return Optional.empty();
            case 3:
                if (params.isAllowTeleport()) {

                    params.setUnstuckStrategy(0);
                    
                    return tryTeleportFallback(bot, context);
                }
                return Optional.empty();

            default:
                return Optional.of(() -> {
                    BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Стратегия не определена. Calibrate.");
                    push(bot, new BotCalibrateTask(bot, "Unstuck strategy undefined!"));
                    params.setUnstuckStrategy(0);
                });
        }
    }

    private Optional<Runnable> trySwimToLand(Bot bot, BotNavigationContext context) {
        if (!BotWorldHelper.isInDanger(bot)) return Optional.empty();
    
        BotPosition target = findNearbyLandFromContext(bot, context);
        if (target == null) return Optional.empty();
    
        return Optional.of(() -> {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🏊 Попытка выплыть на сушу → " + target);
            BotSwimTaskParams swimParams = new BotSwimTaskParams(target);
            BotSwimTask swimTask = new BotSwimTask(bot);
            swimTask.setParams(swimParams);
            push(bot, swimTask);
            params.setUnstuckStrategy(1);
        });
    }

    public BotPosition findNearbyLandFromContext(Bot bot, BotNavigationContext context) {
        if (context == null || context.walkable == null || context.walkable.isEmpty()) return null;
    
        BotBlockData current = bot.getNavigator().getPosition().toBlockData();
    
        return context.walkable.stream()
            .map(BotBlockData::getPosition)
            .filter(pos -> !BlockUtils.isSameBlock(pos.toBlockData(), current)) // избегаем текущей позиции
            .sorted(Comparator.comparingDouble(pos -> BlockUtils.distance(pos, current)))
            .findFirst()
            .orElse(null);
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