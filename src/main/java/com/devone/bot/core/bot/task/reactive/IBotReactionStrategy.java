
package com.devone.bot.core.bot.task.reactive;

import com.devone.bot.core.bot.Bot;

import java.util.Optional;

public interface IBotReactionStrategy {
    Optional<Runnable> check(Bot bot);
    String getName();
}
