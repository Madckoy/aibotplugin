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
        if (candidates == null || candidates.isEmpty())
            return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    public static BotPosition selectSmart(Bot bot, List<BotPosition> candidates, BotNavigationContext context,
            float botYaw) {
                
        if (candidates == null || candidates.isEmpty())
            return null;

        BotPosition best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (BotPosition poi : candidates) {
            double score = BotPoiInterestEvaluator.evaluate(bot, context, poi, botYaw);

            if (score > bestScore) {
                bestScore = score;
                best = poi;
            }
        }

        if (best != null) {
            BotLogger.debug("NAV", true, "Selected POI ➔ " + best + " | interest=" + String.format("%.2f", bestScore));
        }

        return best;
    }

}
