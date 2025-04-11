package com.devone.bot.core.logic.navigation.filters;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.utils.BotBlockData;

public class BotBlockNoUnknownFilter {

    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData block : blocks) {
            if (block.isUnknown()) {
                continue;
            } else {
                result.add(block);
            }
        }
        
        return result;
    }
}
