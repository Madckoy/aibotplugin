
package com.devone.bot.core.bot.task.reactive;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.reactive.reaction.LowHealthReaction;
import com.devone.bot.core.bot.task.reactive.reaction.NearbyHostileReaction;
import com.devone.bot.core.bot.task.reactive.reaction.NearbyPlayerReaction;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Менеджер реактивного поведения бота.
 * Проверяет, нужно ли временно приостановить текущую задачу и выполнить реакцию.
 */
public class BotReactivityManager {

    private static final List<IBotReactionStrategy> strategies = new ArrayList<>();

    static {
        // Добавляем предустановленные стратегии
        strategies.add(new NearbyHostileReaction());
        strategies.add(new LowHealthReaction());
        strategies.add(new NearbyPlayerReaction());
        // сюда можно добавить новые стратегии
    }

    public static Optional<Runnable> checkReactions(Bot bot) {
        for (IBotReactionStrategy strategy : strategies) {
            Optional<Runnable> reaction = strategy.check(bot);
            if (reaction.isPresent()) {
                BotLogger.debug("⚡", true, bot.getId() + " Реакция активирована: " + strategy.getName());
                return reaction;
            }
        }
        return Optional.empty();
    }

    public static void registerStrategy(IBotReactionStrategy strategy) {
        strategies.add(strategy);
    }
}
