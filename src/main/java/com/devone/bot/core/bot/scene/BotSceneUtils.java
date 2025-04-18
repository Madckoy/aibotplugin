
package com.devone.bot.core.bot.scene;

import java.util.List;

import com.devone.bot.core.bot.blocks.BotBlockData;

public class BotSceneUtils {
    public static int estimateHorizontalRadius(List<BotBlockData> blocks) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BotBlockData block : blocks) {
            if (block.getX() < minX) minX = block.getX();
            if (block.getX() > maxX) maxX = block.getX();
            if (block.getZ() < minZ) minZ = block.getZ();
            if (block.getZ() > maxZ) maxZ = block.getZ();
        }
        return Math.max((maxX - minX) / 2, (maxZ - minZ) / 2);
    }
}
