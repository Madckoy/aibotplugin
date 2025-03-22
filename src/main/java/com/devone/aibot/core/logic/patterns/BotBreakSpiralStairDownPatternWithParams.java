package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class BotBreakSpiralStairDownPatternWithParams implements IBotBreakPattern {

    private final int radius;
    private final boolean clockwise;
    private final List<int[]> spiralOffsets;

    public BotBreakSpiralStairDownPatternWithParams(int radius, boolean clockwise) {
        this.radius = radius;
        this.clockwise = clockwise;
        this.spiralOffsets = generateSpiralOffsets(radius, clockwise);
    }

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap, Set<Material> targetMaterials) {
        Location center = bot.getRuntimeStatus().getCurrentLocation();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        List<Location> sorted = geoMap.keySet().stream()
            .filter(loc -> {
                int dx = loc.getBlockX() - cx;
                int dy = cy - loc.getBlockY(); // только ниже
                int dz = loc.getBlockZ() - cz;

                return Math.abs(dx) <= radius && Math.abs(dz) <= radius &&
                       dy >= 0 && dy <= radius * 4 &&
                       !(dx == 0 && dz == 0); // не трогаем центральную колонну
            })
            .filter(loc -> isValidTargetBlock(loc.getBlock().getType(), targetMaterials))
            .sorted(Comparator.comparingInt(loc -> getSpiralOrder(loc, cx, cz, cy)))
            .toList();

        return sorted.isEmpty() ? null : sorted.get(0);
    }

    private int getSpiralOrder(Location loc, int cx, int cz, int cy) {
        int dx = loc.getBlockX() - cx;
        int dz = loc.getBlockZ() - cz;
        int dy = cy - loc.getBlockY(); // чем глубже, тем позже

        for (int i = 0; i < spiralOffsets.size(); i++) {
            int[] offset = spiralOffsets.get(i);
            if (offset[0] == dx && offset[1] == dz) {
                return i + dy * 100;
            }
        }

        return Integer.MAX_VALUE;
    }

    private List<int[]> generateSpiralOffsets(int radius, boolean clockwise) {
        List<int[]> result = new ArrayList<>();
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        int max = (radius * 2 + 1) * (radius * 2 + 1);

        for (int i = 0; i < max; i++) {
            if (Math.abs(x) <= radius && Math.abs(z) <= radius) {
                result.add(new int[]{x, z});
            }

            // Поворот направления (по часовой или против)
            boolean turn = (x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z);
            if (turn) {
                int temp = dx;
                dx = clockwise ? -dz : dz;
                dz = clockwise ? temp : -temp;
            }

            x += dx;
            z += dz;
        }

        return result;
    }

    private boolean isValidTargetBlock(Material type, Set<Material> targetMaterials) {
        return type != Material.AIR && type != Material.WATER && type != Material.LAVA &&
               (targetMaterials == null || targetMaterials.contains(type));
    }

    @Override
    public String getName() {
        return "BotBreakSpiralStairDownPattern_" + (clockwise ? "Clockwise" : "Counter");
    }
}
