package com.devone.bot.core.task.reactive.strategy.example;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.task.reactive.container.example.BotCustomReactiveContainer;
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

        if (bot.getState().getHealth() >= minHealth)
            return Optional.empty();

        BotLogger.debug("🤖", true, bot.getId() + " ✅ Условие выполнено, запускаем контейнер реакции.");

        return Optional.of(() -> {
            BotTaskManager.push(bot, new BotCustomReactiveContainer(bot));
        });
    }

    @Override
    public String getName() {
        return "⚙️ " + reactionName + " (hp < " + minHealth + ", radius = " + detectionRadius + ")";
    }
}
