package com.devone.bot.core.brain.logic.navigator.math.builder;

import java.util.*;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

public class BotReachableSurfaceBuilder {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<BotBlockData> build(List<BotBlockData> navigableBlocks) {
        Map<BotPositionKey, BotBlockData> map = new HashMap<>();
        BotPositionKey startKey = null;

        for (BotBlockData b : navigableBlocks) {
            BotPositionKey key = b.toKey();
            if ("reachable:start".equals(b.getTag())) {
                startKey = key;
            }
            map.put(key, b);
        }

        if (startKey == null) {
            System.out.println("❌ Start block with notes=navigator:start not found.");
            return List.of();
        }

        Set<BotPositionKey> visited = new HashSet<>();
        Queue<BotPositionKey> queue = new LinkedList<>();
        List<BotBlockData> reachable = new ArrayList<>();

        queue.add(startKey);

        while (!queue.isEmpty()) {
            BotPositionKey current = queue.poll();
            if (!visited.add(current)) continue;

            BotBlockData data = map.get(current);
            if (data != null) {
                BotBlockData copy = cloneAndMarkAsReachable(data);
                reachable.add(copy);

                for (int[] d : DELTAS) {
                    int dx = d[0];
                    int dz = d[1];

                    for (int dy = -1; dy <= 1; dy++) {
                        BotPositionKey neighbor = new BotPositionKey(
                            current.getX() + dx,
                            current.getY() + dy,
                            current.getZ() + dz
                        );

                        if (!visited.contains(neighbor) && map.containsKey(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return reachable;
    }

    private static BotBlockData cloneAndMarkAsReachable(BotBlockData original) {
        BotBlockData copy = new BotBlockData();
        copy.setX(original.getX());
        copy.setY(original.getY());
        copy.setZ(original.getZ());
        copy.setType(original.getType());
        copy.setTag("reachable:surface");
        return copy;
    }
}
