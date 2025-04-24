package com.devone.bot.core.brain.logic.navigator.finder;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.*;

public class BotSimplePathFinder {

    private final Set<BotPosition> walkableBlocks;

    public BotSimplePathFinder(Set<BotPosition> walkableBlocks) {
        this.walkableBlocks = walkableBlocks;
    }

    public List<BotPosition> findPath(BotPosition from, BotPosition to) {

        if (from.equals(to)) return List.of(from);

        Queue<BotPosition> queue = new LinkedList<>();
        Map<BotPosition, BotPosition> cameFrom = new HashMap<>();
        Set<BotPosition> visited = new HashSet<>();

        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            BotPosition current = queue.poll();

            // 🧠 Ходим только по реальной проходимой сетке (walkableBlocks)
            for (BotPosition neighbor : SimplePathUtils.getSmartNeighbors(current, walkableBlocks)) {
                if (visited.contains(neighbor)) continue;

                visited.add(neighbor);
                cameFrom.put(neighbor, current);
                queue.add(neighbor);

                if (neighbor.equals(to)) {
                    return reconstructPath(cameFrom, from, to);
                }
            }
        }

        return List.of(); // нет пути
    }

    private List<BotPosition> reconstructPath(
        Map<BotPosition, BotPosition> cameFrom,
        BotPosition start,
        BotPosition end
    ) {
        List<BotPosition> path = new LinkedList<>();
        BotPosition current = end;

        while (!current.equals(start)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }

        path.add(0, start);
        return path;
    }

    // 🧪 Строим путь только к одной случайной цели
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
            BotBlockData block = new BotBlockData();
            block.setPosition(loc);
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
                    BotBlockData block = new BotBlockData();
                    block.setPosition(loc);
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
            BotBlockData placeholder = new BotBlockData();
            placeholder.setPosition(from);
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
                    BotBlockData block = new BotBlockData();
                    block.setPosition(loc);
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
