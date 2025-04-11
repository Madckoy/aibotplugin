package com.devone.bot.core.logic.navigation.filters;

import java.util.*;

import com.devone.bot.utils.BotBlockData;

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
