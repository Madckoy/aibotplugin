package com.devone.bot.core.task.reactive.strategy;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.IBotStrategyReaction;
import com.devone.bot.core.task.reactive.container.BotReactiveNearbyHostileContainer;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.Optional;

public class BotStrategyNearbyHostile implements IBotStrategyReaction {

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLogger.debug("🤖", true, bot.getId() + " 😈 Проверка реакции на близость враждебного моба");

        BotSceneData scene = bot.getBrain().getSceneData();
        BotPosition botPos = bot.getNavigator().getPosition();

        if (scene != null) {
            for (BotBlockData entity : scene.entities) {
                if (BlockUtils.isHostileEntity(entity)==false)
                    continue;
                if (BotWorldHelper.isInDangerousLiquid(entity))
                    continue;

                double dist = botPos.distanceTo(entity.getPosition());
                if (dist >= 1)
                    continue;

                BotLogger.debug("🤖", true, bot.getId() + " ❗ Обнаружен враждебный моб: " + entity.getType()
                        + " (" + String.format("%.1f", dist) + " м)");

                return Optional.of(() -> {
                    BotTaskManager.push(bot, new BotReactiveNearbyHostileContainer(bot, entity));
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
