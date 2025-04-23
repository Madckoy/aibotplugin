package com.devone.bot.core.brain.logic.navigator.math.resolver;

import java.util.*;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotReachabilityResolver {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<BotBlockData> resolve(BotPosition botPos, List<BotBlockData> navigableBlocks) {
        // Начинаем с блока под ботом
        BotPosition start = new BotPosition(botPos.getX(), botPos.getY()-1, botPos.getZ());

        Map<BotPosition, BotBlockData> map = new HashMap<>();
        for (BotBlockData b : navigableBlocks) {
            map.put(new BotPosition(b.getX(), b.getY(), b.getZ()), b);
        }

        Set<BotPosition> visited = new HashSet<>();
        Queue<BotPosition> queue = new LinkedList<>();
        queue.add(start);

        List<BotBlockData> reachable = new ArrayList<>();

        while (!queue.isEmpty()) {
            BotPosition current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            BotBlockData data = map.get(current);
            if (data != null) {
                reachable.add(data);

                for (int[] d : DELTAS) {
                    int dx = d[0];
                    int dz = d[1];

                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = current.getX() + dx;
                        int ny = current.getY() + dy;
                        int nz = current.getZ() + dz;

                        BotPosition neighbor = new BotPosition(nx, ny, nz);
                        if (!visited.contains(neighbor) && map.containsKey(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return reachable;
    }
}
