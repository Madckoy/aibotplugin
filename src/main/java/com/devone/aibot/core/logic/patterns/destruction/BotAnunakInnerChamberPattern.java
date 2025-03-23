package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

public class BotAnunakInnerChamberPattern extends AbstractBotBreakPattern {

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

        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        int tipY = centerY; // Бот стоит на вершине пирамиды
        int height = radius;

        // Наружная оболочка пирамиды
        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;
            int currentY = tipY - y;

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    boolean isShellBlock = Math.abs(x) == layerRadius || Math.abs(z) == layerRadius;
                    if (isShellBlock) {
                        blocksToBreak.add(new Location(world, centerX + x, currentY, centerZ + z));
                    }
                }
            }
        }

        // Центральный блок камеры (в основании)
        int baseY = tipY - height;
        blocksToBreak.add(new Location(world, centerX, baseY, centerZ));

        // Четыре внутренних столба вокруг центра
        int offset = 3;
        for (int[] dir : new int[][] {
                { offset,  offset },
                {-offset,  offset },
                { offset, -offset },
                {-offset, -offset }
        }) {
            for (int i = 0; i < 3; i++) {
                blocksToBreak.add(new Location(
                    world,
                    centerX + dir[0],
                    baseY + i,
                    centerZ + dir[1]
                ));
            }
        }
    }

    @Override
    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }

    @Override
    public String getName() {
        return "BotAnunakInnerChamberPattern";
    }
}
