package com.devone.bot.core.bot.task.reactive.strategy;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.container.BotLowHealthReactionContainer;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotLowHealthStrategy implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        double health = bot.getState().getHealth();
        BotLogger.debug("🤖", true, bot.getId() + " 💔 Проверка реакции на здоровье. HP = " + health);

        if (health >= 5.0) return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug("🤖", true, bot.getId() + " 💔 Срабатывает реакция на низкое здоровье!");
            bot.reactiveTaskStart(new BotLowHealthReactionContainer(bot));
        });
    }

    @Override
    public String getName() {
        return "💔 Критически низкое здоровье";
    }
}
