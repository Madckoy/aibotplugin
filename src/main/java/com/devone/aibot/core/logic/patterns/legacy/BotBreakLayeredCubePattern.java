package com.devone.aibot.core.logic.patterns.legacy;

import com.devone.aibot.core.Bot;

import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class BotBreakLayeredCubePattern extends BotBreakAbstractPattern {

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap) {
        if (!initialized) {
            prepareTargets(bot, geoMap);
            initialized = true;
        }
        return blocksToBreak.poll();
    }

    private void prepareTargets(Bot bot, Map<Location, ?> geoMap) {
        Location center = bot.getRuntimeStatus().getCurrentLocation();
        if (center == null || center.getWorld() == null) return;

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        List<Location> sorted = geoMap.keySet().stream()
            .filter(loc -> {
                int dx = Math.abs(loc.getBlockX() - centerX);
                int dy = Math.abs(loc.getBlockY() - centerY);
                int dz = Math.abs(loc.getBlockZ() - centerZ);
                return dx <= radius && dy <= radius && dz <= radius &&
                       !(dx == 0 && dz == 0); // исключаем центральную колонну
            })
            .sorted(Comparator
                .comparingInt((Location loc) -> Math.abs(loc.getBlockY() - centerY)) // ближе по Y
                .thenComparingDouble(loc -> loc.distanceSquared(center))) // ближе в целом
            .collect(Collectors.toList());

        blocksToBreak.addAll(sorted);
    }

    @Override
    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }

    @Override
    public String getName() {
        return "BotBreakLayeredCubePattern";
    }
}
