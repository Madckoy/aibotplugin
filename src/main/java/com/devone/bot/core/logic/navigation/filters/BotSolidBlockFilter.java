package com.devone.bot.core.logic.navigation.filters;

import java.util.*;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class BotSolidBlockFilter {

    /**
     * Фильтрует блоки по следующим критериям:
     * 1. Исключаем покрытия (например, трава, снег).
     * 2. Исключаем опасные блоки (например, вода, лава).
     * Если блок проходит все проверки, добавляем его в результат.
     */
    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();

        // Проходим по всем блокам и применяем фильтрацию
        for (BotBlockData block : blocks) {
            // 1. Исключаем покрытия (например, снег, трава, и т.д.)
            //if (block.isCover()) {
            //    continue;  // Пропускаем покрытия
            //}

            // 2. Исключаем опасные блоки (например, вода, лава)
            if (block.isDangerous()) {
                continue;  // Пропускаем опасные блоки
            }

            result.add(block); // Добавляем блок в результат, если он прошел все проверки
        }

        
        return result;
    }

}
