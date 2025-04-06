package com.devone.bot.core.logic.navigation;

import java.util.*;

import com.devone.bot.utils.BotCoordinate3D;

public class BotPathfinder {

    public static List<BotCoordinate3D> findPath(BotCoordinate3D start, BotCoordinate3D goal, Set<String> reachableSet) {
        Map<String, BotCoordinate3D> parentMap = new HashMap<>();
        Queue<BotCoordinate3D> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        String startKey = key(start);
        String goalKey = key(goal);

        queue.add(start);
        visited.add(startKey);

        while (!queue.isEmpty()) {
            BotCoordinate3D current = queue.poll();

            if (key(current).equals(goalKey)) {
                return reconstructPath(current, parentMap);
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    // запретить одновременное смещение по x и z (горизонтальная диагональ)
                    if (dx != 0 && dz != 0) continue;
                    for (int dy = -1; dy <= 1; dy++) {
                        // разрешаем смещения по одной оси за раз
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        int nx = current.x + dx;
                        int ny = current.y + dy;
                        int nz = current.z + dz;

                        String neighborKey = nx + "," + ny + "," + nz;

                        if (visited.contains(neighborKey)) continue;
                        if (!reachableSet.contains(neighborKey)) continue;

                        BotCoordinate3D neighbor = new BotCoordinate3D(nx, ny, nz);
                        queue.add(neighbor);
                        visited.add(neighborKey);
                        parentMap.put(neighborKey, current);
                    }
                }
            }
        }

        return Collections.emptyList(); // путь не найден
    }

    private static List<BotCoordinate3D> reconstructPath(BotCoordinate3D end, Map<String, BotCoordinate3D> parentMap) {
        List<BotCoordinate3D> path = new LinkedList<>();
        BotCoordinate3D current = end;

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(key(current));
        }

        return path;
    }

    private static String key(BotCoordinate3D p) {
        return p.x + "," + p.y + "," + p.z;
    }
}
