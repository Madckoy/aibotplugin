package com.devone.bot.core.bot.task.reactive.strategy.example;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.container.BotCustomReactionContainer;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactiveCustomStrategy implements IBotReactionStrategy {

    private final String reactionName;
    private final double minHealth;
    private final double detectionRadius;

    public BotReactiveCustomStrategy(String reactionName, double minHealth, double detectionRadius) {
        this.reactionName = reactionName;
        this.minHealth = minHealth;
        this.detectionRadius = detectionRadius;
    }

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLogger.debug("🤖", true, bot.getId() + " 🧪 Проверка кастомной реакции: " + getName());

        if (bot.getState().getHealth() >= minHealth) return Optional.empty();

        BotLogger.debug("🤖", true, bot.getId() + " ✅ Условие выполнено, запускаем контейнер реакции.");

        return Optional.of(() -> {
            bot.reactiveTaskStart(new BotCustomReactionContainer(bot));
        });
    }

    @Override
    public String getName() {
        return "⚙️ " + reactionName + " (hp < " + minHealth + ", radius = " + detectionRadius + ")";
    }
}
