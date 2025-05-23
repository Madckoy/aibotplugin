package com.devone.bot.core.brain.logic.navigator.math.selector;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.List;
import java.util.Random;

public class BotPOISelector {

    private static final Random random = new Random();

    public static BotPosition selectRandom(List<BotPosition> candidates) {
        if (candidates == null || candidates.isEmpty())
            return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    public static BotPosition selectSmart(Bot bot, List<BotPosition> pois, BotNavigationContext context ) {
                
        if (pois == null || pois.isEmpty())
            return null;

        BotPosition best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (BotPosition poi : pois) {
            double score = BotPoiInterestEvaluator.evaluate(bot, poi, context);

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
