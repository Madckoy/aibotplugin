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
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🧭 Проверка на необходимость симуляции по кругу.");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " В движении: " + bot.getNPCNavigator().isNavigating());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Текущая рекомендация: "+bot.getNavigator().getSuggestion());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигатор занят? "+bot.getNavigator().isCalculating());



        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_SIMULATE ) return Optional.empty();
        if(bot.getNPCNavigator().isNavigating()) return Optional.empty();
      
        return Optional.of(() -> {
            BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 Расчет лучшего угла");
            bot.getNavigator().setSuggestion(Suggestion.NAVIGATION_CHANGE_DIRECTION);
        });  

    }

        @Override
    public String getName() {
        return ICON+" Расчет лучшего угла";
    }
    
    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true ;
    }
}
