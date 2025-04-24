
package com.devone.bot.core.task.reactive;

import com.devone.bot.core.Bot;

import java.util.Optional;

public interface IBotStrategyReaction {
    Optional<Runnable> check(Bot bot);
    String getName();
}
