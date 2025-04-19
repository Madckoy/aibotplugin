package com.devone.bot.core.bot.task.reactive.reaction;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.sequence.BotReactiveSequenceTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 📍 Шаблон реактивной стратегии для быстрого копирования.
 * Заменить логику внутри check().
 */
public class BotReactiveTemplateReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {

        BotLogger.debug("🤖", true, bot.getId() + " 🔍 Проверка реакции: " + getName());

        // 💡 Проверка условия (пример: здоровье, мобы, игроки рядом и т.п.)
        boolean condition = false; // заменить на свою логику

        if (!condition) return Optional.empty();

        // 🔁 Проверка, не выполняется ли уже реакция
        if (BotReactiveUtils.isAlreadyReacting(bot)) {
            BotLogger.debug("🤖", true, bot.getId() + " 🔁 Уже выполняется реакция — выходим");
            return BotReactiveUtils.avoidOverReaction(bot);
        }

        // ✅ Активация реакции
        BotReactiveUtils.activateReaction(bot);

        return Optional.of(() -> {
            BotLogger.debug("🤖", true, bot.getId() + " 🚀 Запуск реактивной цепочки: " + getName());

            List<BotTask<?>> tasks = new ArrayList<>();

            // TODO: Добавь свои задачи
            // tasks.add(new YourTask1(...));
            // tasks.add(new YourTask2(...));

            BotReactiveSequenceTask sequence = new BotReactiveSequenceTask(bot, tasks);
            BotUtils.pushTask(bot, sequence);
        });
    }

    @Override
    public String getName() {
        return "🧪 Пример реакции (замени название)";
    }
}
