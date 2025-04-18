package com.devone.bot.core.logic.navigation.filters;

import java.util.*;

import com.devone.bot.core.bot.blocks.BotBlockData;
import com.devone.bot.core.bot.blocks.BotLocation;

public class BotBlocksNavigableFilter {

    /* 
     * Оставляет только те точки, к которым можно перейти хотя бы с одной соседней.
     */
    public static List<BotBlockData> filter(List<BotBlockData> walkableBlocks) {
        Map<BotLocation, BotBlockData> map = new HashMap<>();
        for (BotBlockData block : walkableBlocks) {
            map.put(new BotLocation(block.getX(), block.getY(), block.getZ()), block);
        }

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : walkableBlocks) {
            if (hasNavigableNeighbor(block, map)) {
                result.add(block);
            }
        }

        return result;
    }

    private static boolean hasNavigableNeighbor(BotBlockData block, Map<BotLocation, BotBlockData> map) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue; // Пропускаем саму точку
                    if (Math.abs(dy) > 1) continue; // Прыжки на 2 и более блоков запрещены

                    BotLocation neighbor = new BotLocation(x + dx, y + dy, z + dz);
                    if (map.containsKey(neighbor)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
