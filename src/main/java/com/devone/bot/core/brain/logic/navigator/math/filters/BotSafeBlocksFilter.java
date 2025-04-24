package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.*;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotSafeBlocksFilter {
    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData block : blocks) {
            if (block.isDangerous()) {
                continue; 
            }
            result.add(block);
        }
        return result;
    }

}
