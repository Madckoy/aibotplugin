package com.devone.bot.core.bot.task.reactive;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.reactive.strategy.BotLowHealthStrategy;
import com.devone.bot.core.bot.task.reactive.strategy.BotNearbyHostileStrategy;
import com.devone.bot.core.bot.task.reactive.strategy.BotNearbyPlayerStrategy;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 🧠 Менеджер реактивного поведения бота.
 * Проверяет, нужно ли временно приостановить задачу и выполнить реакцию.
 */
public class BotReactivityManager {

    private static final List<IBotReactionStrategy> strategies = new ArrayList<>();

    static {
        // 📚 Регистрация стандартных стратегий
        registerStrategy(new BotNearbyHostileStrategy());
        registerStrategy(new BotLowHealthStrategy());
        registerStrategy(new BotNearbyPlayerStrategy());

        BotLogger.debug("🧠", true, "🧩 Зарегистрированы предустановленные реакции: " + strategies.size());
    }

    public static Optional<Runnable> checkReactions(Bot bot) {
        if (bot.getActiveTask() == null) {
            BotLogger.debug("🧠", true, bot.getId() + " ⭕ Нет активной задачи для проверки реакций.");
            return Optional.empty();
        }

        BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " 🧩 Проверка реакций...");

        for (IBotReactionStrategy strategy : strategies) {
            BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " 🔎 Пробуем стратегию: " + strategy.getName());

            Optional<Runnable> reaction = strategy.check(bot);

            if (reaction.isPresent()) {
                BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " ✅ Реакция сработала: " + strategy.getName());
                return reaction;
            }
        }

        BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " ❌ Реакции не сработали.");
        return Optional.empty();
    }

    public static void registerStrategy(IBotReactionStrategy strategy) {
        strategies.add(strategy);
    }
}
