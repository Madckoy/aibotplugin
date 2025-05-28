package com.devone.bot.core.brain.cortex.reaction.example;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.example.BotSequenceTemplate;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactionTemplate implements IBotReaction {

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 🔍 Проверка шаблонной реакции: " + getName());

        // 💡 Здесь своё условие
        boolean condition = false;

        if (!condition)
            return Optional.empty();

        return Optional.of(() -> {
            BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 🚀 Триггер шаблонной реакции: " + getName());
            BotTaskManager.push(bot, new BotSequenceTemplate(bot)); // ✅ Сахар
        });
    }

    @Override
    public String getName() {
        return "🧪 Пример реакции (замени название)";
    }
}
