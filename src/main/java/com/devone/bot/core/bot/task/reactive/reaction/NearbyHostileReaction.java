package com.devone.bot.core.bot.task.reactive.reaction;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.bot.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.sequence.BotReactiveSequenceTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NearbyHostileReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {

        BotReactiveUtils.activateReaction(bot);

        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();
        BotLocation botPos = bot.getNavigation().getLocation();

        if (sceneData != null) {
            for (BotBlockData entity : sceneData.entities) {
                if (!entity.isHostileMob()) continue;

                double dist = botPos.distanceTo(entity.getLocation());
                if (dist < 5 && !BotWorldHelper.isInDangerousLiquid(entity)) {
                    // 💡 Начинаем реакцию: телепорт → атака
                    return Optional.of(() -> {
                        BotLogger.debug("⚠️", true, bot.getId() + " Враг слишком близко. Начинаем реактивную последовательность!");

                        // 1. Телепорт
                        BotTeleportTask tpTask = new BotTeleportTask(bot, null); // w/o player
                        BotTeleportTaskParams tpParams = new BotTeleportTaskParams(entity);
                        tpTask.setParams(tpParams);

                        // 2. Атака
                        BotHandAttackTask atkTask = new BotHandAttackTask(bot);
                        BotHandAttackTaskParams atkParams = new BotHandAttackTaskParams();
                        atkParams.setTarget(entity);
                        atkTask.setParams(atkParams);

                        // ⛓️ Создаём цепочку
                        List<BotTask<?>> tasks = new ArrayList<>();
                        tasks.add(tpTask);
                        tasks.add(atkTask);

                        BotReactiveSequenceTask sequence = new BotReactiveSequenceTask(bot, tasks);
                        BotUtils.pushTask(bot, sequence);
                    });
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Близость враждебного моба";
    }
}
