package com.devone.bot.core.brain.logic.navigator.math.filters;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.ArrayList;
import java.util.List;

public class BotAddStartNavigationFilter {

    public static List<BotBlockData> apply(BotPosition botPosition, List<BotBlockData> blocks) {
        if (blocks == null) return List.of();

        List<BotBlockData> result = new ArrayList<>(blocks);

        // Стартовая точка — под ботом (координаты ног)
        BotPosition pos = new BotPosition(botPosition.getX(), botPosition.getY() - 1, botPosition.getZ());
        BotBlockData start = new BotBlockData();
        start.setPosition(pos);
        start.setType("DUMMY");
        start.setTag("navigator:start");

        result.add(start);
        return result;
    }
}
