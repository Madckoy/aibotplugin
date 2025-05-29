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


public class BotReactionRotateToBestAngle implements IBotReaction {
    private static final String ICON = "📐";
    
    @Override
    public Optional<Runnable> validate(Bot bot) {

        BotLogger.debug("🤖", bot.isLogged(), bot.getId() + "Проверка реакции на рекомендацию поискать лучший угол");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Проверка на препятствия рядом");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " В движении: " + bot.getNPCNavigator().isNavigating());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Текущая рекомендация: "+bot.getNavigator().getSuggestion());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигатор занят? "+bot.getNavigator().isCalculating());


        if (bot.getNavigator().getSuggestion() != Suggestion.NAVIGATION_CHANGE_DIRECTION) return Optional.empty();
        if(bot.getNPCNavigator().isNavigating()) return Optional.empty();

        return Optional.of(() -> {
            try {

                int radius = BotConstants.DEFAULT_SCAN_RADIUS;
                
                Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readValueTyped(bot, 
                                            BotMemoryPartition.PartitionKey.NAVIGATION, 
                                            BotMemoryItem.ItemKey.SCAN_RADIUS, Integer.class);     

                if(scanRadiusFromMem!=null) {
                    radius = scanRadiusFromMem.intValue();
                }
        
                BotSimulatorResult res = bot.getNavigator().simulate(
                    BotConstants.DEFAULT_NORMAL_SIGHT_FOV, 
                    radius, 
                    BotConstants.DEFAULT_SCAN_HEIGHT
                );                

                if (res.status) {
                    BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 есть хороший угол зрения. Поворачиваем туда!" + res.yaw);
                    BotUtils.rotate(bot.getActiveTask(), bot, res.yaw);
                    bot.getNavigator().setSuggestion(Suggestion.NAVIGATION_CALCULATE);

                    BotMemoryV2Utils.memorizeScanRadius(bot, radius);

                } else {
                    BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 Нет подходящего угла зрения!");
                    if (!bot.getNavigator().getCandidates().isEmpty()) {
                        bot.getNavigator().setSuggestion(Suggestion.NONE);
                    } else {
                        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 📐 Застрял жестко. Пробуем ждать...");
                        bot.getNavigator().setSuggestion(Suggestion.NONE);
                    }
                }
            } catch (Exception e) {
                BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🆘 Симуляция не прошла! Ошибка: "+e.getMessage());
                bot.getNavigator().setSuggestion(Suggestion.NONE);
            }
        });
    }

    @Override
    public String getName() {
        return " Find the best view angle";
    }

    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true ;
    }
}
