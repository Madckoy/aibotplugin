
package com.devone.bot.core.bot.brain.reactivity;

import com.devone.bot.core.bot.Bot;

import java.util.Optional;

public interface IBotReactionStrategy {
    Optional<Runnable> check(Bot bot);
    String getName();
}
