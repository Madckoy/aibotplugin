package com.devone.bot.core.bot.brain.analysis;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.utils.world.BotWorldHelper;

public class BotBrainAnalysis {
    public static boolean isInRiskyEnvironment(Bot bot) {
        return BotWorldHelper.isInDangerousLiquid(bot) || bot.getState().isLowHealth(20.0);
    }

    public static boolean shouldAvoidExcavation(Bot bot) {
        return BotWorldHelper.isNearWater(bot) || BotWorldHelper.isOnUnstableGround(bot);
    }

    public static boolean isLikelyToFallInHole(Bot bot) {
        // возможно позже сюда попадет логика анализа сцены
        return false;
    }
}