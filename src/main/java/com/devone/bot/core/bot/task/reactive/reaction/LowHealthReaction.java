
package com.devone.bot.core.bot.task.reactive.reaction;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class LowHealthReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        
        BotReactiveUtils.activateReaction(bot);

        double health = bot.getState().getHealth(); // Пример: нужно иметь метод getHealth()

        if (health < 5.0) {
            return Optional.of(() -> {
                BotLogger.debug("💔", true, "Здоровье критически низкое. Ищу безопасное место...");
                        });
            }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Критически низкое здоровье";
    }
}
