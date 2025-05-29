package com.devone.bot.core.brain.cortex.reaction;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactionMoveTo implements IBotReaction {
    private static final String ICON = "🦶";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Проверка на движение. ");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " В движении: " + bot.getNPCNavigator().isNavigating());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Текущая рекомендация: "+bot.getNavigator().getSuggestion());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигатор занят? "+bot.getNavigator().isCalculating());

        if(bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_MOVE) return Optional.empty();
        if(bot.getNPCNavigator().isNavigating()) return Optional.empty();

        return Optional.of(() -> {
                    BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигация в предложенную координату.");
                    try {
                        bot.getNavigator().setTarget(bot.getNavigator().getSuggestedTarget());
                        bot.getNavigator().navigate(1.5f);
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🦶 Бот начал движение к цели.");
                    } catch (Exception e) {
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🆘 Ошибка при навигации!");
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Бот не смог сдвинуться");
                    }
        });
    }

        @Override
    public String getName() {
        return " Moving to position";
    }

    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true ;
    }
}
