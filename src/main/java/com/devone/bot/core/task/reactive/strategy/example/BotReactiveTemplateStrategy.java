package com.devone.bot.core.task.reactive.strategy.example;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.task.reactive.container.example.BotTemplateReactiveContainer;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactiveTemplateStrategy implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLogger.debug("🤖", true, bot.getId() + " 🔍 Проверка шаблонной реакции: " + getName());

        // 💡 Здесь своё условие
        boolean condition = false;

        if (!condition)
            return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug("🤖", true, bot.getId() + " 🚀 Триггер шаблонной реакции: " + getName());
            BotTaskManager.push(bot, new BotTemplateReactiveContainer(bot)); // ✅ Сахар
        });
    }

    @Override
    public String getName() {
        return "🧪 Пример реакции (замени название)";
    }
}
