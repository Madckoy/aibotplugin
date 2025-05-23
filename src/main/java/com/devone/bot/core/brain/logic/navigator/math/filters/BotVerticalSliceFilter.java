package com.devone.bot.core.brain.logic.navigator.math.filters;


import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotVerticalSliceFilter {
    public static List<BotBlockData> filter(List<BotBlockData> blocks, double botY, double range) {
        List<BotBlockData> result = new ArrayList<>();
        
        for (BotBlockData block : blocks) {
            // Проверяем, что блок находится в пределах range от botY
            if ((block.getY() >= botY - range) && (block.getY() <= botY + range)) {
                result.add(block);
            }
        }
        
        return result;
    }
}
