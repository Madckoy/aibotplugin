package com.devone.bot.core.brain.cortex.reaction;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.utils.logger.BotLogger;



public class BotReactionNavigationSimulate implements IBotReaction {
    private static final String ICON = "🧭";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_SIMULATE || bot.getNPCNavigator().isNavigating()) {            
            return Optional.empty();
        }
        
        return Optional.of(() -> {
            BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 Расчет лучшего угла");
            bot.getNavigator().setSuggestion(Suggestion.NAVIGATION_CHANGE_DIRECTION);
        });   
    }

        @Override
    public String getName() {
        return ICON+" Расчет лучшего угла";
    }
}
