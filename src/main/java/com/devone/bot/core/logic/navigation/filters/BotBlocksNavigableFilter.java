package com.devone.bot.core.logic.navigation.filters;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

import java.util.*;

public class BotBlocksNavigableFilter {

    /* 
     * Оставляет только те точки, к которым можно перейти хотя бы с одной соседней.
     */
    public static List<BotBlockData> filter(List<BotBlockData> walkableBlocks) {
        Map<BotCoordinate3D, BotBlockData> map = new HashMap<>();
        for (BotBlockData block : walkableBlocks) {
            map.put(new BotCoordinate3D(block.x, block.y, block.z), block);
        }

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : walkableBlocks) {
            if (hasNavigableNeighbor(block, map)) {
                result.add(block);
            }
        }

        return result;
    }

    private static boolean hasNavigableNeighbor(BotBlockData block, Map<BotCoordinate3D, BotBlockData> map) {
        int x = block.x;
        int y = block.y;
        int z = block.z;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue; // Пропускаем саму точку
                    if (Math.abs(dy) > 1) continue; // Прыжки на 2 и более блоков запрещены

                    BotCoordinate3D neighbor = new BotCoordinate3D(x + dx, y + dy, z + dz);
                    if (map.containsKey(neighbor)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
