package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;

import java.util.Map;

public class BotBreakInversePyramidPattern extends AbstractBotBreakPattern {

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap) {
        if (!initialized) {
            generatePattern(bot.getRuntimeStatus().getCurrentLocation());
            initialized = true;
        }
        return blocksToBreak.poll();
    }

    private void generatePattern(Location center) {
        if (center == null || center.getWorld() == null) return;

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        int y = centerY;

        for (int r = radius; r >= 0; r--) {
            int minX = centerX - r;
            int maxX = centerX + r;
            int minZ = centerZ - r;
            int maxZ = centerZ + r;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    // Исключаем центральную колонну
                    if (x == centerX && z == centerZ) continue;

                    blocksToBreak.add(new Location(center.getWorld(), x, y, z));
                }
            }

            y--; // следующий уровень вниз
        }
    }

    @Override
    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }

    @Override
    public String getName() {
        return "InversePyramidPattern";
    }
}
