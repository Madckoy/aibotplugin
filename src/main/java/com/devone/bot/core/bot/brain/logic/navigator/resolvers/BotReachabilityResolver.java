package com.devone.bot.core.bot.brain.logic.navigator.resolvers;

import java.util.*;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;

public class BotReachabilityResolver {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<BotBlockData> resolve(BotLocation botPos, List<BotBlockData> navigableBlocks) {
        // Начинаем с блока под ботом
        BotLocation start = new BotLocation(botPos.getX(), botPos.getY()-1, botPos.getZ());

        Map<BotLocation, BotBlockData> map = new HashMap<>();
        for (BotBlockData b : navigableBlocks) {
            map.put(new BotLocation(b.getX(), b.getY(), b.getZ()), b);
        }

        Set<BotLocation> visited = new HashSet<>();
        Queue<BotLocation> queue = new LinkedList<>();
        queue.add(start);

        List<BotBlockData> reachable = new ArrayList<>();

        while (!queue.isEmpty()) {
            BotLocation current = queue.poll();
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

                        BotLocation neighbor = new BotLocation(nx, ny, nz);
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
