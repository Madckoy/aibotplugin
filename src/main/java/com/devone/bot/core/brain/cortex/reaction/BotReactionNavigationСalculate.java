package com.devone.bot.core.brain.cortex.reaction;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;


public class BotReactionNavigationСalculate implements IBotReaction {
    private static final String ICON = "🧭";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_CALCULATE) return Optional.empty();

        return Optional.of(() -> {
            if (!bot.getNavigator().isCalculating()) {
                
                bot.getNavigator().setEnabled(true);

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
                    // TODO: handle exception
                }
                    
            }
        });
    }

        @Override
    public String getName() {
        return ICON+" Расчет навигации";
    }
}
