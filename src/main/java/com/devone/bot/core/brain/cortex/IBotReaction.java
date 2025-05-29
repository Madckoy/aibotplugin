
package com.devone.bot.core.brain.cortex;

import com.devone.bot.core.Bot;

import java.util.Optional;

public interface IBotReaction {
    Optional<Runnable> validate(Bot bot);
    String getName();
    /** По умолчанию реакция не рерывает остальные */
    default boolean shouldInterrupt(Bot bot) {
        return false;
    }

}
