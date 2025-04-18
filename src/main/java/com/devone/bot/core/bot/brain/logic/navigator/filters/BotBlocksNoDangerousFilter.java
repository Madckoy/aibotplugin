package com.devone.bot.core.bot.brain.logic.navigator.filters;

import java.util.*;

import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;

public class BotBlocksNoDangerousFilter {
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
