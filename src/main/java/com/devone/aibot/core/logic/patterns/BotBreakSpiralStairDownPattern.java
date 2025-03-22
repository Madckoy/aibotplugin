package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public class BotBreakSpiralStairDownPattern implements IBotBreakPattern {

    private final int radius;
    private final List<int[]> spiralOffsets;

    public BotBreakSpiralStairDownPattern(int radius) {
        this.radius = radius;
        this.spiralOffsets = generateSpiralOffsets(radius);
    }

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap, Set<Material> targetMaterials) {
        Location center = bot.getRuntimeStatus().getCurrentLocation();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        List<Location> sorted = geoMap.keySet().stream()
            .filter(loc -> isValidTargetBlock(loc.getBlock().getType(), targetMaterials))
            .filter(loc -> {
                int dx = loc.getBlockX() - centerX;
                int dz = loc.getBlockZ() - centerZ;
                int dy = centerY - loc.getBlockY(); // только ниже

                return Math.abs(dx) <= radius && Math.abs(dz) <= radius && dy >= 0 && dy <= radius * 4 &&
                       !(dx == 0 && dz == 0); // не трогаем колонну
            })
            .sorted(Comparator.comparingInt(loc -> getSpiralOrder(loc, center)))
            .collect(Collectors.toList());

        return sorted.isEmpty() ? null : sorted.get(0);
    }

    private int getSpiralOrder(Location loc, Location center) {
        int dx = loc.getBlockX() - center.getBlockX();
        int dz = loc.getBlockZ() - center.getBlockZ();
        int dy = center.getBlockY() - loc.getBlockY(); // глубже = позже

        // Находим индекс смещения в спирали
        for (int i = 0; i < spiralOffsets.size(); i++) {
            int[] offset = spiralOffsets.get(i);
            if (offset[0] == dx && offset[1] == dz) {
                return i + dy * 100; // Учитываем глубину
            }
        }

        return Integer.MAX_VALUE;
    }

    private boolean isValidTargetBlock(Material type, Set<Material> targetMaterials) {
        return type != Material.AIR && type != Material.WATER && type != Material.LAVA &&
               (targetMaterials == null || targetMaterials.contains(type));
    }

    private List<int[]> generateSpiralOffsets(int radius) {
        List<int[]> result = new ArrayList<>();
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        int max = (radius * 2 + 1) * (radius * 2 + 1);

        for (int i = 0; i < max; i++) {
            if (Math.abs(x) <= radius && Math.abs(z) <= radius) {
                result.add(new int[]{x, z});
            }

            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int temp = dx;
                dx = -dz;
                dz = temp;
            }

            x += dx;
            z += dz;
        }

        return result;
    }

    @Override
    public String getName() {
        return "BotBreakSpiralStairDownPattern";
    }
}
