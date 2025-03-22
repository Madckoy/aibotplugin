package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public class BotBreakLayeredCubePattern implements IBotBreakPattern {

    private final int radius;

    public BotBreakLayeredCubePattern(int radius) {
        this.radius = radius;
    }

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap, Set<Material> targetMaterials) {
        Location center = bot.getRuntimeStatus().getCurrentLocation();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        List<Location> candidates = geoMap.keySet().stream()
            .filter(loc -> isValidTargetBlock(loc.getBlock().getType(), targetMaterials))
            .filter(loc -> {
                int dx = Math.abs(loc.getBlockX() - centerX);
                int dy = Math.abs(loc.getBlockY() - centerY);
                int dz = Math.abs(loc.getBlockZ() - centerZ);
                return dx <= radius && dy <= radius && dz <= radius &&
                       !(dx == 0 && dz == 0); // исключаем центральную вертикаль
            })
            .sorted(Comparator
                .comparingInt((Location loc) -> Math.abs(loc.getBlockY() - centerY)) // сначала текущий слой, потом вверх и вниз
                .thenComparingDouble(loc -> loc.distance(center))) // от центра к краям
            .collect(Collectors.toList());

        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private boolean isValidTargetBlock(Material type, Set<Material> targetMaterials) {
        return type != Material.AIR && type != Material.WATER && type != Material.LAVA &&
               (targetMaterials == null || targetMaterials.contains(type));
    }

    @Override
    public String getName() {
        return "BotBreakLayeredCubePattern";
    }
}
