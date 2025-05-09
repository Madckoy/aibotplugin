package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.*;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

public class BotNavigableFilter {

    /**
     * Оставляет только те точки, к которым можно перейти хотя бы с одной соседней.
     */
    public static List<BotBlockData> filter(List<BotBlockData> walkableBlocks) {
        Map<BotPositionKey, BotBlockData> map = new HashMap<>();
        for (BotBlockData block : walkableBlocks) {
            map.put(block.toKey(), block);
        }

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : walkableBlocks) {
            if (hasNavigableNeighbor(block, map)) {
                result.add(block);
            }
        }

        return result;
    }

    private static boolean hasNavigableNeighbor(BotBlockData block, Map<BotPositionKey, BotBlockData> map) {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();

        for (double dx = -1; dx <= 1; dx++) {
            for (double dy = -1; dy <= 1; dy++) {
                for (double dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (Math.abs(dy) > 1) continue;

                    BotPositionKey neighborKey = new BotPositionKey(x + dx, y + dy, z + dz);
                    if (map.containsKey(neighborKey)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
