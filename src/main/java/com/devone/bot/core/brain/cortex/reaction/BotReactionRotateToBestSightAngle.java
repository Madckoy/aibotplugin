package com.devone.bot.core.brain.cortex.reaction;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.navigator.simulator.BotSimulatorResult;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;


public class BotReactionRotateToBestSightAngle implements IBotReaction {
    private static final String ICON = "📐";
    
    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNavigator().getSuggestion() != Suggestion.CHANGE_DIRECTION) return Optional.empty();

        int radius = BotConstants.DEFAULT_SCAN_RADIUS;
        
        Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readValueTyped(bot, 
                                    BotMemoryPartition.PartitionKey.NAVIGATION, 
                                    BotMemoryItem.ItemKey.SCAN_RADIUS, Integer.class);     

        if(scanRadiusFromMem!=null) {
            radius = scanRadiusFromMem.intValue();
        }

        final int rds  = radius;

        return Optional.of(() -> {
            try {
                BotSimulatorResult res = bot.getNavigator().simulate(
                    BotConstants.DEFAULT_NORMAL_SIGHT_FOV, 
                    rds, 
                    BotConstants.DEFAULT_SCAN_HEIGHT
                );

                if (res.status) {
                    BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 есть хороший угол зрения. Поворачиваем туда!");
                    BotUtils.rotate(bot.getActiveTask(), bot, res.yaw);
                } else {
                    BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 Нет подходящего угла зрения!");
                    if (!bot.getNavigator().getCandidates().isEmpty()) {
                        bot.getNavigator().setSuggestion(Suggestion.MOVE);
                    } else {
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 Застрял жестко. Пробуем ждать...");
                    }
                }
            } catch (Exception e) {
                BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🆘 Симуляция не прошла!");
            }
        });
    }

    @Override
    public String getName() {
        return ICON+" Поворт на лучший угол зрения";
    }
}
