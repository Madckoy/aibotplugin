package com.devone.bot.core.brain.logic.navigator.math.builder;

import java.util.*;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotReachableSurfaceBuilder {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    /**
     * Находит все блоки, достижимые с позиции бота по навигационной поверхности.
     * Возвращает новые объекты с пометками.
     */
    public static List<BotBlockData> build(BotPosition botPos, List<BotBlockData> navigableBlocks) {
        BotPosition start = new BotPosition(botPos.getX(), botPos.getY()-1, botPos.getZ());

        Map<BotPosition, BotBlockData> map = new HashMap<>();
        for (BotBlockData b : navigableBlocks) {
            map.put(new BotPosition(b.getX(), b.getY(), b.getZ()), b);
        }

        if (!map.containsKey(start)) {
            System.out.println("❌ Start position not found in navigable surface: " + start);
            return List.of();
        }

        Set<BotPosition> visited = new HashSet<>();
        Queue<BotPosition> queue = new LinkedList<>();
        List<BotBlockData> reachable = new ArrayList<>();

        queue.add(start);

        while (!queue.isEmpty()) {
            BotPosition current = queue.poll();
            if (!visited.add(current)) continue;

            BotBlockData data = map.get(current);
            if (data != null) {
                BotBlockData copy = cloneAndMarkAsReachable(data);
                reachable.add(copy);

                for (int[] d : DELTAS) {
                    int dx = d[0];
                    int dz = d[1];

                    for (int dy = -1; dy <= 1; dy++) {
                        BotPosition neighbor = new BotPosition(
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
        copy.setType("REACHABLE");
        copy.setNotes("reachable:surface");
        return copy;
    }
}
