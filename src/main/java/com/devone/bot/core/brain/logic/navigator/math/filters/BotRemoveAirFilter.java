package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotRemoveAirFilter {

    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData block : blocks) {
            if (block.isAir()) {
                continue;
            } else {
                result.add(block);
            }
        }
        return result;
    }
}
