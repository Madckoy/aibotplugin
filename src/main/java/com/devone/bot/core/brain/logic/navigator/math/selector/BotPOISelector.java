package com.devone.bot.core.brain.logic.navigator.math.selector;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BotPOISelector {

    private static final Random random = new Random();

    public static BotPosition selectRandom(List<BotPosition> candidates) {
        if (candidates == null || candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    public static BotPosition selectSmart(Bot bot, List<BotPosition> candidates, BotNavigationContext context, float botYaw) {
        if (candidates == null || candidates.isEmpty()) return null;
    
        List<BotPosition> sorted = candidates.stream()
            .sorted(Comparator.comparingDouble(poi -> {
                double score = BotPoiInterestEvaluator.evaluate(bot, context, poi, botYaw);
                if (Double.isNaN(score) || Double.isInfinite(score)) {
                    BotLogger.debug("NAV", true, "Invalid interest value detected for POI: " + poi);
                    return Double.NEGATIVE_INFINITY;
                }
                return -score;
            }))
            .collect(Collectors.toList());
    
        // 🔥 Показываем ТОП-5 для дебага
        BotLogger.debug("NAV", true, "Top 5 POI by interest:");
        int limit = Math.min(5, sorted.size());
        for (int i = 0; i < limit; i++) {
            BotPosition poi = sorted.get(i);
            double interest = BotPoiInterestEvaluator.evaluate(bot, context, poi, botYaw);
    
            BotLogger.debug("NAV", true,
                (i + 1) + ". " + poi +
                " | interest=" + String.format("%.2f", interest)
            );
        }
    
        BotPosition best = sorted.isEmpty() ? null : sorted.get(0);
    
        if (best != null) {
            double interest = BotPoiInterestEvaluator.evaluate(bot, context, best, botYaw);
    
            BotLogger.debug(
                "NAV", true,
                "Selected POI ➔ " + best + 
                " | interest=" + String.format("%.2f", interest)
            );
        }
    
        return best;
    }
    
    
}
