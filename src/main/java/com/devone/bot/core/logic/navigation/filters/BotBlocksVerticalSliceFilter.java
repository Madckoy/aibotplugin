package com.devone.bot.core.logic.navigation.filters;


import java.util.ArrayList;
import java.util.List;

import com.devone.bot.utils.blocks.BotBlockData;

public class BotBlocksVerticalSliceFilter {
    public static List<BotBlockData> filter(List<BotBlockData> blocks, int botY, int range) {
        List<BotBlockData> result = new ArrayList<>();
        
        for (BotBlockData block : blocks) {
            // Проверяем, что блок находится в пределах range от botY
            if ((block.y >= botY - range) && (block.y <= botY + range)) {
                result.add(block);
            }
        }
        
        return result;
    }
}
