package com.devone.bot.core.brain.cortex.reaction;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;


public class BotReactionNavigationСalculate implements IBotReaction {
    private static final String ICON = "🧭";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_CALCULATE || bot.getNPCNavigator().isNavigating()) return Optional.empty();

        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Проверка на необходимость расчетов для текущего yaw");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " В движении: " + bot.getNPCNavigator().isNavigating());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Текущая рекомендация: "+bot.getNavigator().getSuggestion());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигатор занят? "+bot.getNavigator().isCalculating());

        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_CALCULATE ) return Optional.empty();
        if(bot.getNPCNavigator().isNavigating()) return Optional.empty();

        return Optional.of(() -> {
               
                int radius = BotConstants.DEFAULT_SCAN_RADIUS;
                        
                Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readValueTyped(bot, 
                                                    BotMemoryPartition.PartitionKey.NAVIGATION, 
                                                    BotMemoryItem.ItemKey.SCAN_RADIUS, Integer.class);     

                if(scanRadiusFromMem!=null) {
                    radius = scanRadiusFromMem.intValue();
                }

                final int rds  = radius;

                try {
                    bot.getNavigator().calculate(BotConstants.DEFAULT_NORMAL_SIGHT_FOV, rds , BotConstants.DEFAULT_SCAN_HEIGHT);   
                } catch (Exception e) {
                    BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " ⛔ Unable to calculate!");
                }
        });
    }

        @Override
    public String getName() {
        return " Calculate best view angle";
    }
    
    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true ;
    }
}
