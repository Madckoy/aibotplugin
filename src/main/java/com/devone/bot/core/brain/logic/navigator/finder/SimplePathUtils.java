package com.devone.bot.core.brain.logic.navigator.finder;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplePathUtils {

    public static List<BotPosition> getSmartNeighbors(BotPosition current, Set<BotPositionKey> walkableKeys) {
        List<BotPosition> result = new ArrayList<>();

        int[][] directions = {
            { 1, 0}, {-1, 0},
            { 0, 1}, { 0, -1}
        };

        for (int[] d : directions) {
            int dx = d[0];
            int dz = d[1];

            for (double dy = -1; dy <= 1; dy++) {
                double x = current.getX() + dx;
                double y = current.getY() + dy;
                double z = current.getZ() + dz;

                BotPosition neighbor = new BotPosition(x, y, z);
                BotPositionKey key = neighbor.toKey();

                if (walkableKeys.contains(key)) {
                    result.add(neighbor);
                }
            }
        }

        return result;
    }

    public static Set<BotPositionKey> toKeySet(List<BotBlockData> blocks) {
        Set<BotPositionKey> result = new HashSet<>();
        for (BotBlockData b : blocks) {
            result.add(b.toKey());
        }
        return result;
    }
}
