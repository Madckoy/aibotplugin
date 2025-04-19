package com.devone.bot.core.bot.task.reactive;

import java.util.Optional;

import com.devone.bot.core.bot.Bot;

public class BotReactiveUtils {


    public static void activateReaction(Bot bot) {
        bot.getBrain().setReactionInProgress(true);
    }

    public static Optional<Runnable> avoidOverReaction(Bot bot) {
        return Optional.empty();
    }
    
    public static boolean isAlreadyReacting(Bot bot) {
        return bot.getBrain().isReactionInProgress();
    }
}
