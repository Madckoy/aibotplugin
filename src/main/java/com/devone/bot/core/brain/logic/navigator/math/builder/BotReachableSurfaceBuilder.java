package com.devone.bot.core.brain.logic.navigator.math.builder;

import java.util.*;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotReachableSurfaceBuilder {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<BotBlockData> build(List<BotBlockData> navigableBlocks) {
        Optional<BotBlockData> optionalStart = navigableBlocks.stream()
            .filter(b -> "navigator:start".equals(b.getNotes()))
            .findFirst();
    
        if (optionalStart.isEmpty()) {
            System.out.println("❌ Start block with notes=navigator:start not found.");
            return List.of();
        }
    
        BotBlockData startBlock = optionalStart.get();
        BotPosition start = new BotPosition(startBlock.getX(), startBlock.getY(), startBlock.getZ());
    
        Set<BotPosition> visited = new HashSet<>();
        Queue<BotPosition> queue = new LinkedList<>();
        List<BotBlockData> reachable = new ArrayList<>();
    
        queue.add(start);
    
        while (!queue.isEmpty()) {
            BotPosition current = queue.poll();
            if (!visited.add(current)) continue;
    
            List<BotBlockData> blocksAtCurrent = findBlocksAt(navigableBlocks, current);
            for (BotBlockData data : blocksAtCurrent) {
                BotBlockData copy = cloneAndMarkAsReachable(data);
                reachable.add(copy);
            }
    
            for (int[] d : DELTAS) {
                int dx = d[0];
                int dz = d[1];
    
                for (int dy = -1; dy <= 1; dy++) {
                    BotPosition neighbor = new BotPosition(
                        current.getX() + dx,
                        current.getY() + dy,
                        current.getZ() + dz
                    );
    
                    if (!visited.contains(neighbor) && hasBlockAt(navigableBlocks, neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    
        return reachable;
    }

    private static boolean hasBlockAt(List<BotBlockData> list, BotPosition pos) {
        return list.stream().anyMatch(b ->
            b.getX() == pos.getX() &&
            b.getY() == pos.getY() &&
            b.getZ() == pos.getZ());
    }

    private static List<BotBlockData> findBlocksAt(List<BotBlockData> list, BotPosition pos) {
        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData b : list) {
            if (b.getX() == pos.getX() &&
                b.getY() == pos.getY() &&
                b.getZ() == pos.getZ()) {
                result.add(b);
            }
        }
        return result;
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
