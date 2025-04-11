package com.devone.bot.core.logic.navigation.resolvers;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

import java.util.*;

public class BotReachabilityResolver {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<BotBlockData> resolve(BotCoordinate3D botPos, List<BotBlockData> navigableBlocks) {
        // Начинаем с блока под ботом
        BotCoordinate3D start = new BotCoordinate3D(botPos.x, botPos.y - 1, botPos.z);

        Map<BotCoordinate3D, BotBlockData> map = new HashMap<>();
        for (BotBlockData b : navigableBlocks) {
            map.put(new BotCoordinate3D(b.x, b.y, b.z), b);
        }

        Set<BotCoordinate3D> visited = new HashSet<>();
        Queue<BotCoordinate3D> queue = new LinkedList<>();
        queue.add(start);

        List<BotBlockData> reachable = new ArrayList<>();

        while (!queue.isEmpty()) {
            BotCoordinate3D current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            BotBlockData data = map.get(current);
            if (data != null) {
                reachable.add(data);

                for (int[] d : DELTAS) {
                    int dx = d[0];
                    int dz = d[1];

                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = current.x + dx;
                        int ny = current.y + dy;
                        int nz = current.z + dz;

                        BotCoordinate3D neighbor = new BotCoordinate3D(nx, ny, nz);
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
