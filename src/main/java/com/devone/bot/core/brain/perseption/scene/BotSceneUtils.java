
package com.devone.bot.core.brain.perseption.scene;

import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotSceneUtils {
    public static double estimateHorizontalRadius(List<BotBlockData> blocks) {
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;

        for (BotBlockData block : blocks) {
            if (block.getX() < minX) minX = block.getX();
            if (block.getX() > maxX) maxX = block.getX();
            if (block.getZ() < minZ) minZ = block.getZ();
            if (block.getZ() > maxZ) maxZ = block.getZ();
        }
        return Math.max((maxX - minX) / 2, (maxZ - minZ) / 2);
    }
}
