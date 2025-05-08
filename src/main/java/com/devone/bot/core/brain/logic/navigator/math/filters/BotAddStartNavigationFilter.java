package com.devone.bot.core.brain.logic.navigator.math.filters;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.ArrayList;
import java.util.List;

public class BotAddStartNavigationFilter {

    public static List<BotBlockData> apply(BotPosition botPosition, List<BotBlockData> blocks) {
        if (blocks == null) return List.of();

        List<BotBlockData> result = new ArrayList<>(blocks);

        BotBlockData start = new BotBlockData();
        start.setX((int) botPosition.getX());
        start.setY((int) botPosition.getY() - 1);
        start.setZ((int) botPosition.getZ());
        start.setType("DUMMY");
        start.setTag("navigator:start");

        result.add(start);

        return result;
    }
}
