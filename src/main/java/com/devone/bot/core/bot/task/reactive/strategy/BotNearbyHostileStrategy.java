package com.devone.bot.core.bot.task.reactive.strategy;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.container.BotNearbyHostileReactiveContainer;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.Optional;

public class BotNearbyHostileStrategy implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLogger.debug("🤖", true, bot.getId() + " 😈 Проверка реакции на близость враждебного моба");

        BotSceneData scene = bot.getBrain().getMemory().getSceneData();
        BotLocation botPos = bot.getNavigation().getLocation();

        if (scene != null) {
            for (BotBlockData entity : scene.entities) {
                if (!entity.isHostileMob())
                    continue;
                if (BotWorldHelper.isInDangerousLiquid(entity))
                    continue;

                double dist = botPos.distanceTo(entity.getLocation());
                if (dist >= 5)
                    continue;

                BotLogger.debug("🤖", true, bot.getId() + " ❗ Обнаружен враждебный моб: " + entity.getType()
                        + " (" + String.format("%.1f", dist) + " м)");

                return Optional.of(() -> {
                    bot.pushReactiveTask(new BotNearbyHostileReactiveContainer(bot, entity));
                });
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "😈 Близость враждебного моба";
    }
}
