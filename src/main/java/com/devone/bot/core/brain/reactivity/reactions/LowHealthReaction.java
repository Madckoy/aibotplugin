
package com.devone.bot.core.brain.reactivity.reactions;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.brain.reactivity.IBotReactionStrategy;
import com.devone.bot.utils.logger.BotLogger;

import java.util.Optional;

public class LowHealthReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        double health = bot.getState().getHealth(); // Пример: нужно иметь метод getHealth()

        if (health < 5.0) {
            return Optional.of(() -> {
                BotLogger.info("💔", true, "Здоровье критически низкое. Ищу безопасное место...");
                        });
            }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Критически низкое здоровье";
    }
}
