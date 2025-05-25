
package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;

import java.util.Optional;

public interface IBotReaction {
    Optional<Runnable> validate(Bot bot);
    String getName();
}
