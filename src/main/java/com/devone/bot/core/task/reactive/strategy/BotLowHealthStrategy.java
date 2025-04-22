package com.devone.bot.core.task.reactive.strategy;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.task.reactive.container.BotLowHealthReactiveContainer;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotLowHealthStrategy implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        double health = bot.getState().getHealth();
        BotLogger.debug("🤖", true, bot.getId() + " 💔 Проверка реакции на здоровье. HP = " + health);

        if (health >= 5.0)
            return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug("🤖", true, bot.getId() + " 💔 Срабатывает реакция на низкое здоровье!");
            BotTaskManager.push(bot, new BotLowHealthReactiveContainer(bot));
        });
    }

    @Override
    public String getName() {
        return "💔 Критически низкое здоровье";
    }
}
