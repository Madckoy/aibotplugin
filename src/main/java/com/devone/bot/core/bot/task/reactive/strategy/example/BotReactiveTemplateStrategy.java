package com.devone.bot.core.bot.task.reactive.strategy.example;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.container.example.BotTemplateReactionContainer;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactiveTemplateStrategy implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLogger.debug("🤖", true, bot.getId() + " 🔍 Проверка шаблонной реакции: " + getName());

        // 💡 Здесь своё условие
        boolean condition = false;

        if (!condition) return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug("🤖", true, bot.getId() + " 🚀 Триггер шаблонной реакции: " + getName());
            bot.reactiveTaskStart(new BotTemplateReactionContainer(bot)); // ✅ Сахар
        });
    }

    @Override
    public String getName() {
        return "🧪 Пример реакции (замени название)";
    }
}
