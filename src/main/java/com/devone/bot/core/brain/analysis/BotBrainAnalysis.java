package com.devone.bot.core.brain.analysis;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotBrainAnalysis {

    public static boolean shouldAvoidExcavation(Bot bot) {
        return BotWorldHelper.isNearWater(bot) || BotWorldHelper.isOnUnstableGround(bot);
    }

    public static boolean isLikelyToFallInHole(Bot bot) {
        // возможно позже сюда попадет логика анализа сцены
        return false;
    }
}