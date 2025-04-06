package com.devone.bot.core.logic.navigation;


import java.util.List;
import java.util.stream.Collectors;

import com.devone.bot.utils.BotBlockData;

public class BotVerticalRangeFilter {
    public static List<BotBlockData> trimByYRange(List<BotBlockData> blocks, int botY, int range) {
        return blocks.stream()
            .filter(block -> Math.abs(block.y - botY) <= range)
            .collect(Collectors.toList());
    }
}
