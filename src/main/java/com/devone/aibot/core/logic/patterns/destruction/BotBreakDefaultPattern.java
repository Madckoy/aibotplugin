package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import java.util.Map;

public class BotBreakDefaultPattern extends AbstractBotBreakPattern {

    @Override
    public Location findNextBlock(Bot bot, Map<Location, ?> geoMap) {
        if (!initialized) {
            generatePattern(bot.getRuntimeStatus().getCurrentLocation(), geoMap);
            initialized = true;
        }
        return blocksToBreak.poll();
    }

    private void generatePattern(Location center, Map<Location, ?> geoMap) {
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        for (int y = centerY; y >= centerY - radius; y--) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
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
        return "BotBreakDefaultPattern";
    }
}
