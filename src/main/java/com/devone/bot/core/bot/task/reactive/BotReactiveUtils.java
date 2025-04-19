package com.devone.bot.core.bot.task.reactive;

import java.util.Optional;

import com.devone.bot.core.bot.Bot;

public class BotReactiveUtils {

    public static Optional<Runnable> activateReaction(Bot bot) {

        if (!bot.getBrain().isReactionInProgress()) {
            bot.getBrain().setReactionInProgress(true);
        }
        return Optional.empty();
    }
}
