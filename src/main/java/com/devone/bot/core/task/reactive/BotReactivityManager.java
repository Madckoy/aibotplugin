package com.devone.bot.core.task.reactive;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.reactive.strategy.BotStrategyLowHealth;
import com.devone.bot.core.task.reactive.strategy.BotStrategyNearbyHostile;
import com.devone.bot.core.task.reactive.strategy.BotStrategyNearbyPlayer;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 🧠 Менеджер реактивного поведения бота.
 * Проверяет, нужно ли временно приостановить задачу и выполнить реакцию.
 */
public class BotReactivityManager {

    private static final List<IBotStrategyReaction> strategies = new ArrayList<>();

    static {
        // 📚 Регистрация стандартных стратегий
        registerStrategy(new BotStrategyNearbyHostile());
        registerStrategy(new BotStrategyLowHealth());
        registerStrategy(new BotStrategyNearbyPlayer());

        BotLogger.debug("🧠", true, "🧩 Зарегистрированы предустановленные реакции: " + strategies.size());
    }

    public static Optional<Runnable> checkReactions(Bot bot) {
        if (bot.getActiveTask() == null) {
            BotLogger.debug("🧠", true, bot.getId() + " ⭕ Нет активной задачи для проверки реакций.");
            return Optional.empty();
        }

        BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " 🧩 Проверка реакций...");

        for (IBotStrategyReaction strategy : strategies) {
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

    public static void registerStrategy(IBotStrategyReaction strategy) {
        strategies.add(strategy);
    }
}
