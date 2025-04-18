package com.devone.bot.core.logic.task.brain;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.blocks.BotBlockData;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.brain.behaviour.BotBehaviorSelector;
import com.devone.bot.core.bot.brain.behaviour.BotTaskCandidate;
import com.devone.bot.core.bot.brain.behaviour.BotTaskCandidatesFactory;
import com.devone.bot.core.bot.scene.BotSceneData;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.core.logic.navigation.selectors.BotBlockSelector;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.brain.params.BotBrainTaskParams;
import com.devone.bot.core.logic.task.excavate.BotExcavateTask;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.logic.task.idle.BotIdleTask;
import com.devone.bot.core.logic.task.sonar.BotSonar3DTask;
import com.devone.bot.core.logic.task.teleport.BotTeleportTask;
import com.devone.bot.core.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotBrainTask extends BotTaskAutoParams<BotBrainTaskParams> {

    public BotBrainTask(Bot bot) {
        super(bot, null, BotBrainTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotBrainTaskParams> setParams(BotBrainTaskParams params) {
        if (params == null) {
            throw new IllegalArgumentException("BotBrainTask: setParams(...) получил null");
        }
    
        super.setParams(params); // 🔑 обязательно вызываем суперкласс
    
        // 🎯 Устанавливаем визуальные атрибуты
        setIcon(params.getIcon() != null ? params.getIcon() : "🧠");
        setObjective(params.getObjective() != null ? params.getObjective() : "Think");
    
        // 🎯 Визуально можно логировать флаги (опционально для отладки)
        BotLogger.debug("🧠 BotBrainTask", isLogging(), bot.getId() + " параметры загружены: " +
            "explore=" + params.isAllowExploration() + ", " +
            "excavate=" + params.isAllowExcavation() + ", " +
            "violence=" + params.isAllowViolence() + ", " +
            "teleport=" + params.isAllowTeleport()
        );
    
        return this;
    }

    @Override
    public void execute() {
        BotLogger.info(icon, this.isLogging(), "The bot "+ bot.getId() + " is making a decision...");
        //
        int thinkingTicks = bot.getBrain().getThinkingTicks();

        if (thinkingTicks > 50) {
            BotLogger.warn(icon, this.isLogging(), bot.getId() + 
                " 🧠 Бот думает слишком долго (" + thinkingTicks + " тиков). Сброс в Idle.");
            
            push(bot, new BotIdleTask(bot));
            bot.getBrain().resetThinkingCycle(); // сбросим счётчик
            return; // выходим из execute()
        }
        //
        bot.getBrain().markThinkingCycle();
        //
        //long currentTime = System.currentTimeMillis();
        //
        long removed = bot.getBrain().getMemory().cleanup(params.getMemoryExpirationMillis());
        
        BotLogger.info(icon, this.isLogging(), bot.getId() + " Removed outdated navigation points: " + removed);
        
        //
        // осматриваемся и обновляем картинку мира
        //
        //
        //if(currentTime-lastScanTime > 1000) {
            BotSonar3DTask sonar = new BotSonar3DTask(bot);
            sonar.execute();
            System.currentTimeMillis();
            //return;
        //}
        // 💡 Блокируем мышление, если сцена не готова
        if (bot.getBrain().getMemory().getSceneData() == null) {
            BotLogger.info(icon, isLogging(), bot.getId() + " ⛔ Ожидаем результаты сканирования...");
            return;
        }
        //
        // 📌  Start Thinking
        //
        Runnable decision = determineBehaviorScenario(bot);
        
        if (decision != null) {
            decision.run();
        }
        //
        //
        //
        return;
    }
    
    private void push(Bot bot, BotTask<?> task) {
        bot.getLifeCycle().getTaskStackManager().pushTask(task);
        bot.getBrain().resetThinkingCycle();
        BotLogger.info(icon, this.isLogging(), "The task is pushed to stack ");
    }

    private Runnable determineBehaviorScenario(Bot bot) {
        boolean stuck = bot.getState().isStuck();
    
        if (stuck) {
            Optional<Runnable> unstuck = tryUnstuckStrategy(bot);
            if (unstuck.isPresent()) return unstuck.get();
    
            return () -> {
                BotLogger.info(icon, isLogging(), bot.getId() + " 💤 Бот застрял. Уходим в Idle.");
                push(bot, new BotIdleTask(bot));
            };
        }
    
        Runnable weighted = pickWeightedTask(bot);
        if (weighted != null) return weighted;
    
        return () -> {
            BotLogger.info(icon, isLogging(), bot.getId() + " 💤 Нет задач. Уходим в Idle.");
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
            case 1: // Excavation
                if (params.isAllowExcavation()) {
                    return Optional.of(() -> {
                        BotLogger.info(icon, this.isLogging(), bot.getId() + " The bot is stuck. Starting Excavation to unstuck");
                        BotExcavateTask task = new BotExcavateTask(bot);
                        BotExcavateTaskParams params = new BotExcavateTaskParams();
                        params.setOffsetY(params.getOuterRadius()-1);



                        params.setPatternName("cone.yml");
                        task.setParams(params);
                        push(bot, task);
                    });
                } else {
                    return Optional.empty();
                }
    
            case 2: // Teleport with fallback
                if (params.isAllowTeleport()) {
                    BotLocation botPos = bot.getNavigation().getLocation();
                    BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();
                    BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities, botPos);
                    
                    // tryTeleportFallback сам вернёт Optional<Runnable>
                    return tryTeleportFallback(bot, context);
                } else {
                    return Optional.empty();
                }
    
            default:
                return Optional.of(() -> {
                    BotLogger.info(icon, isLogging(), bot.getId() + " Falling back to idle behavior.");
                    push(bot, new BotIdleTask(bot));
                });
        }
    }

    private Optional<Runnable> tryTeleportFallback(Bot bot, BotSceneContext context) {
        List<Supplier<Optional<Runnable>>> attempts = List.of(
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

        BotLogger.info("🚫", this.isLogging(), bot.getId() + " All teleport attempts were failed.");
        return Optional.empty();
    }

    private Optional<Runnable> tryTeleportToReachable(Bot bot, List<BotBlockData> data){
        if(data!=null && data.size()>0){
            return Optional.empty();
        }

        BotBlockData block = BotBlockSelector.pickRandomTarget(data);

        // Возврат задачи в виде Runnable
        return Optional.of(() -> {
                    BotLogger.info("⚡", this.isLogging(), "Телепорт: любая достижимая точка навигации:" + bot.getId() + block);
                    // Создание параметров
                    BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
                    // Создание задачи и передача параметров
                    BotTeleportTask tpTask = new BotTeleportTask(bot, null);
                    tpTask.setParams(tpParams); // Теперь параметры корректно передаются
                    push(bot, tpTask);
            });
    }

    private Optional<Runnable> tryTeleportToNavigable(Bot bot, List<BotBlockData> data){
        if(data!=null && data.size()>0){
            return Optional.empty();
        }

        BotBlockData block = BotBlockSelector.pickRandomTarget(data);

        // Возврат задачи в виде Runnable
        return Optional.of(() -> {
                    BotLogger.info("⚡", this.isLogging(), "Телепорт: любая точка навигации:" + bot.getId() + block);
                    // Создание параметров
                    BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
                    // Создание задачи и передача параметров
                    BotTeleportTask tpTask = new BotTeleportTask(bot, null);
                    tpTask.setParams(tpParams); // Теперь параметры корректно передаются
                    push(bot, tpTask);
        });
    }

    
    private Optional<Runnable> tryTeleportToWalkable(Bot bot, List<BotBlockData> data){
        if(data!=null && data.size()>0){
            return Optional.empty();
        }

        BotBlockData block = BotBlockSelector.pickRandomTarget(data);

        // Возврат задачи в виде Runnable
        return Optional.of(() -> {
            BotLogger.info("⚡", this.isLogging(), "Телепорт: любая проходимая точка :" + bot.getId() + block);
            // Создание параметров
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            // Создание задачи и передача параметров
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams); // Теперь параметры корректно передаются
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToEntity(Bot bot, List<BotBlockData> data){
        if(data!=null && data.size()>0){
            return Optional.empty();
        }

        BotBlockData block = BotBlockSelector.pickRandomTarget(data);

        // Возврат задачи в виде Runnable
        return Optional.of(() -> {
            BotLogger.info("⚡", this.isLogging(), "Телепорт: существо в пределах доступности:" + bot.getId() + block);
            // Создание параметров
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(block);
            // Создание задачи и передача параметров
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams); // Теперь параметры корректно передаются
            push(bot, tpTask);
        });
    }

    private Optional<Runnable> tryTeleportToSpawn(Bot bot){
        BotLocation spawnLocation = BotWorldHelper.getWorldSpawnLocation();
        // Возврат задачи в виде Runnable
        return Optional.of(() -> {
            BotLogger.info("⚡", this.isLogging(), "Телепорт: точка респавна:" + bot.getId() + spawnLocation);
            // Создание параметров
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams(spawnLocation);
            // Создание задачи и передача параметров
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams); // Теперь параметры корректно передаются
            push(bot, tpTask);
        });
    }

}