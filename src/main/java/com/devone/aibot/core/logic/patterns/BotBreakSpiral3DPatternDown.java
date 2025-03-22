package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BotBreakSpiral3DPatternDown implements IBotBreakPattern {

    private final int radius;

    public BotBreakSpiral3DPatternDown(int radius) {
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
                int dx = loc.getBlockX() - centerX;
                int dy = loc.getBlockY() - centerY;
                int dz = loc.getBlockZ() - centerZ;

                if (Math.abs(dx) > radius || Math.abs(dy) > radius || Math.abs(dz) > radius)
                    return false;

                return !(dx == 0 && dz == 0); // оставляем центральную колонну
            })
            .sorted(Comparator.comparingDouble(loc -> spiral3DDistance(center, loc)))
            .toList();

        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private boolean isValidTargetBlock(Material type, Set<Material> targetMaterials) {
        return type != Material.AIR && type != Material.WATER && type != Material.LAVA &&
               (targetMaterials == null || targetMaterials.contains(type));
    }

    private double spiral3DDistance(Location center, Location loc) {
        int dx = loc.getBlockX() - center.getBlockX();
        int dy = loc.getBlockY() - center.getBlockY();
        int dz = loc.getBlockZ() - center.getBlockZ();

        double flatDistance = Math.sqrt(dx * dx + dz * dz);
        return flatDistance + (Math.abs(dy) * 0.25);
    }

    @Override
    public String getName() {
        return "Spiral3DPattern";
    }
}
