package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotBlocksNoCoverFilter {

    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();

        // Проходим по всем блокам и применяем фильтрацию
        for (BotBlockData block : blocks) {
            // 1. Исключаем покрытия (например, снег, трава, и т.д.)
            if (block.isCover()) {
                continue;  // Пропускаем покрытия
            } else {
                result.add(block); // Добавляем блок в результат, если он прошел все проверки
            }
        }
        
        return result;
    }
}
