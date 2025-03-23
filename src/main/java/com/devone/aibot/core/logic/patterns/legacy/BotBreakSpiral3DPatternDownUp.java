package com.devone.aibot.core.logic.patterns.legacy;

import com.devone.aibot.core.Bot;

import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class BotBreakSpiral3DPatternDownUp extends BotBreakAbstractPattern {

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap) {
        if (!initialized) {
            generatePattern(bot.getRuntimeStatus().getCurrentLocation(), geoMap);
            initialized = true;
        }

        return blocksToBreak.poll();
    }

    private void generatePattern(Location center, Map<Location, ?> geoMap) {
        if (center == null || center.getWorld() == null) return;

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        List<Location> sorted = geoMap.keySet().stream()
            .filter(loc -> {
                int dx = loc.getBlockX() - centerX;
                int dy = loc.getBlockY() - centerY;
                int dz = loc.getBlockZ() - centerZ;

                if (Math.abs(dx) > radius || Math.abs(dy) > radius || Math.abs(dz) > radius)
                    return false;

                // ❗ Исключаем только строго центральную колонну
                return !(dx == 0 && dz == 0);
            })
            .sorted(Comparator.comparingDouble(loc -> spiralPriority(center, loc)))
            .collect(Collectors.toList());

        blocksToBreak.addAll(sorted);
    }

    private double spiralPriority(Location center, Location loc) {
        int dx = loc.getBlockX() - center.getBlockX();
        int dy = loc.getBlockY() - center.getBlockY();
        int dz = loc.getBlockZ() - center.getBlockZ();

        double horizontal = Math.sqrt(dx * dx + dz * dz);
        return horizontal + Math.abs(dy) * 0.25;
    }

    @Override
    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }

    @Override
    public String getName() {
        return "BotBreakSpiral3DPatternDownUp";
    }
}
