package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;

import java.util.Map;

public class BotAnunakSolidPyramidPattern extends AbstractBotBreakPattern {

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

        int fullSize = radius * 2 + 1;

        for (int y = centerY - radius; y <= centerY; y++) {
            int layer = centerY - y;
            int pyramidHalf = layer;

            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {

                    // Пропускаем блоки, находящиеся внутри объема пирамиды
                    if (Math.abs(x - centerX) <= pyramidHalf && Math.abs(z - centerZ) <= pyramidHalf) {
                        continue;
                    }

                    blocksToBreak.add(new Location(center.getWorld(), x, y, z));
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
        return "BotAnunakSolidPyramidPattern";
    }
}
