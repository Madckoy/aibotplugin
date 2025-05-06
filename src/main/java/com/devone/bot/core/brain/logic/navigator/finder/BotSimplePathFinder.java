package com.devone.bot.core.brain.logic.navigator.finder;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionKey;

import java.util.*;

public class BotSimplePathFinder {

    private final Set<BotPositionKey> walkableKeys;

    public BotSimplePathFinder(Set<BotPositionKey> walkableKeys) {
        this.walkableKeys = new HashSet<>(walkableKeys);
    }

    public List<BotPosition> findPath(BotPosition from, BotPosition to) {
        BotPositionKey fromKey = from.toKey();
        BotPositionKey toKey = to.toKey();

        if (fromKey.equals(toKey)) return List.of(from);

        Queue<BotPosition> queue = new LinkedList<>();
        Map<BotPositionKey, BotPosition> cameFrom = new HashMap<>();
        Set<BotPositionKey> visited = new HashSet<>();

        queue.add(from);
        visited.add(fromKey);

        while (!queue.isEmpty()) {
            BotPosition current = queue.poll();
            //BotPositionKey currentKey = current.toKey();

            for (BotPosition neighbor : SimplePathUtils.getSmartNeighbors(current, walkableKeys)) {
                BotPositionKey neighborKey = neighbor.toKey();
                if (visited.contains(neighborKey)) continue;

                visited.add(neighborKey);
                cameFrom.put(neighborKey, current);
                queue.add(neighbor);

                if (neighborKey.equals(toKey)) {
                    return reconstructPath(cameFrom, fromKey, toKey);
                }
            }
        }

        return List.of(); // нет пути
    }

    private List<BotPosition> reconstructPath(
        Map<BotPositionKey, BotPosition> cameFrom,
        BotPositionKey start,
        BotPositionKey end
    ) {
        List<BotPosition> path = new LinkedList<>();
        BotPositionKey currentKey = end;

        while (!currentKey.equals(start)) {
            BotPosition step = cameFrom.get(currentKey);
            if (step == null) break; // fallback
            path.add(0, step);
            currentKey = step.toKey();
        }

        path.add(0, new BotPosition(start.getX(), start.getY(), start.getZ()));
        return path;
    }

    public static List<BotBlockData> buildDebugPathBlocks(
        BotPosition from,
        List<BotBlockData> navTargets,
        BotSimplePathFinder pathfinder
    ) {
        if (navTargets == null || navTargets.isEmpty()) return List.of();

        BotBlockData target = navTargets.get(new Random().nextInt(navTargets.size()));
        List<BotPosition> path = pathfinder.findPath(from, target.getPosition());

        if (path == null || path.isEmpty()) return List.of();

        List<BotBlockData> result = new ArrayList<>();
        for (BotPosition loc : path) {
            BotBlockData block = new BotBlockData(loc.getX(), loc.getY(), loc.getZ());
            block.setType("DUMMY");
            block.setTag("debug:path");
            result.add(block);
        }

        return result;
    }

    public static List<BotBlockData> buildAllDebugPaths(
        BotPosition from,
        List<BotBlockData> navTargets,
        BotSimplePathFinder pathfinder
    ) {
        List<BotBlockData> result = new ArrayList<>();
        if (navTargets == null || navTargets.isEmpty()) {
            System.out.println("⚠️ No navigation targets provided.");
            return result;
        }

        int successCount = 0;

        for (BotBlockData target : navTargets) {
            if (target == null) continue;

            List<BotPosition> path = pathfinder.findPath(from, target.getPosition());
            if (path != null && !path.isEmpty()) {
                for (BotPosition loc : path) {
                    BotBlockData block = new BotBlockData(loc.getX(), loc.getY(), loc.getZ());
                    block.setType("DUMMY");
                    block.setTag("debug:path");
                    result.add(block);
                }
                System.out.println("✅ Path to: " + target);
                successCount++;
            } else {
                System.out.println("❌ Failed path to: " + target);
            }
        }

        if (successCount == 0) {
            BotBlockData placeholder = new BotBlockData(from.getX(), from.getY(), from.getZ());
            placeholder.setType("DUMMY");
            placeholder.setTag("debug:path");
            result.add(placeholder);
            System.out.println("⚠️ No valid path found — inserting placeholder block for debug.");
        } else {
            System.out.println("🔍 Total valid paths: " + successCount);
        }

        return result;
    }

    public static List<List<BotBlockData>> buildAllDebugPathsV2(
        BotPosition from,
        List<BotBlockData> navTargets,
        BotSimplePathFinder pathfinder
    ) {
        List<List<BotBlockData>> allPaths = new ArrayList<>();
        if (navTargets == null || navTargets.isEmpty()) {
            System.out.println("⚠️ No targets available for path building.");
            return allPaths;
        }

        for (BotBlockData target : navTargets) {
            if (target == null) continue;

            List<BotPosition> path = pathfinder.findPath(from, target.getPosition());
            if (path != null && !path.isEmpty()) {
                List<BotBlockData> debugBlocks = new ArrayList<>();
                for (BotPosition loc : path) {
                    BotBlockData block = new BotBlockData(loc.getX(), loc.getY(), loc.getZ());
                    block.setType("DUMMY");
                    block.setTag("debug:path");
                    debugBlocks.add(block);
                }
                allPaths.add(debugBlocks);
                System.out.println("✅ Path to: " + target);
            } else {
                System.out.println("❌ Failed path to: " + target);
            }
        }

        System.out.println("🔍 Total valid paths: " + allPaths.size());
        return allPaths;
    }
}
