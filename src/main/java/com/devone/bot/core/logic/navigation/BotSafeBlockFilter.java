package com.devone.bot.core.logic.navigation;

import java.util.*;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class BotSafeBlockFilter {

    public static final Set<String> UNSAFE_TYPES = Set.of(
        "AIR", "CAVE_AIR", "VOID_AIR", "WATER", "LAVA", "CACTUS", "FIRE", "MAGMA_BLOCK"
    );

    public static List<BotCoordinate3D> extractSafeBlocks(List<BotBlockData> blocks) {
        return blocks.stream()
                .filter(b -> !UNSAFE_TYPES.contains(b.type.toUpperCase()))
                .map(b -> new BotCoordinate3D(b.x, b.y, b.z))
                .toList();
    }

    public static List<BotCoordinate3D> extractUnsafeBlocks(List<BotBlockData> blocks) {
        return blocks.stream()
                .filter(b -> UNSAFE_TYPES.contains(b.type.toUpperCase()))
                .map(b -> new BotCoordinate3D(b.x, b.y, b.z))
                .toList();
    }

    public static List<BotCoordinate3D> extractReachableBlocksFromBot(List<BotBlockData> blocks, BotCoordinate3D bot) {
        Map<String, BotBlockData> blockMap = new HashMap<>();
        Set<String> unsafeSet = new HashSet<>();
        Set<String> visited = new HashSet<>();
        List<BotCoordinate3D> reachable = new ArrayList<>();

        for (BotBlockData b : blocks) {
            String key = b.x + "," + b.y + "," + b.z;
            blockMap.put(key, b);
            if (UNSAFE_TYPES.contains(b.type.toUpperCase())) {
                unsafeSet.add(key);
            }
        }

        Queue<BotCoordinate3D> queue = new LinkedList<>();
        BotCoordinate3D start = new BotCoordinate3D(bot.x, bot.y, bot.z);
        queue.add(start);
        visited.add(bot.x + "," + bot.y + "," + bot.z);

        while (!queue.isEmpty()) {
            BotCoordinate3D current = queue.poll();
            reachable.add(current);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;

                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = current.x + dx;
                        int ny = current.y + dy;
                        int nz = current.z + dz;

                        String key = nx + "," + ny + "," + nz;
                        String headKey = nx + "," + (ny + 1) + "," + nz;

                        if (visited.contains(key)) continue;
                        if (!blockMap.containsKey(key)) continue;
                        if (unsafeSet.contains(key)) continue;

                        // Проверка на наличие воздуха над головой
                        if (blockMap.containsKey(headKey) && !UNSAFE_TYPES.contains(blockMap.get(headKey).type.toUpperCase())) {
                            continue;
                        }

                        // Перепад высоты должен быть допустимым
                        if (Math.abs(ny - current.y) > 1) continue;

                        visited.add(key);
                        queue.add(new BotCoordinate3D(nx, ny, nz));
                    }
                }
            }
        }

        return reachable;
    }
}