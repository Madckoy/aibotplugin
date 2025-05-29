package com.devone.bot.core.brain.cortex.reaction;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactionMoveTo implements IBotReaction {
    private static final String ICON = "🦶";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_MOVE || bot.getNPCNavigator().isNavigating()) return Optional.empty();

        return Optional.of(() -> {
                if(bot.getNavigator().isCalculating()==false) {
                    BotBlockData target = bot.getNavigator().getSuggestedTarget();
                    bot.getNavigator().setTarget(target);
                    try {
                        bot.getNavigator().navigate(1.5f);
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🦶 Начал движение к цели.");
                    } catch (Exception e) {
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🆘 Ошибка при навигации!");
                    }
                }
        });
    }

        @Override
    public String getName() {
        return ICON+" Двигаемся в точку навигации";
    }
}
