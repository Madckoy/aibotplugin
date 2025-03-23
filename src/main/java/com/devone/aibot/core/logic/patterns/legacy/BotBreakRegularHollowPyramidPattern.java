package com.devone.aibot.core.logic.patterns.legacy;

import com.devone.aibot.core.Bot;

import org.bukkit.Location;

import java.util.Map;

public class BotBreakRegularHollowPyramidPattern extends BotBreakAbstractPattern {

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

        for (int layer = 0; layer <= radius; layer++) {
            int y = centerY - layer;
            int edge = layer;

            int minX = centerX - edge;
            int maxX = centerX + edge;
            int minZ = centerZ - edge;
            int maxZ = centerZ + edge;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x == centerX && z == centerZ) continue; // ⚠️ Столбик анунаков остаётся

                    Location loc = new Location(center.getWorld(), x, y, z);
                    if (geoMap.containsKey(loc)) {
                        blocksToBreak.add(loc);
                    }
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }

    @Override
    public String getName() {
        return "BotBreakRegularHollowPyramidPattern";
    }
}
