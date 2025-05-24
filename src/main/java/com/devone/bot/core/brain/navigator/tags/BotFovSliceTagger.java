package com.devone.bot.core.brain.navigator.tags;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;
import com.devone.bot.core.utils.blocks.BotPositionSight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotFovSliceTagger {

    /**
     * Тегирует все блоки по всей высоте, попадающие в радиус и угол обзора от бота.
     */
    
    public static int tagFovSliceAll(List<BotBlockData> blocks,
                                    BotPositionSight bot,
                                    double fovDeg,
                                    int radius,
                                    int height) {

        if (blocks == null || blocks.isEmpty()) return 0;

        double botX = bot.getX();
        double botY = bot.getY();
        double botZ = bot.getZ();

        int minY = (int) Math.floor(botY - height);
        int maxY = (int) Math.floor(botY + 1 + height);

        float yaw = bot.getYaw() % 360;
        if (yaw < 0) yaw += 360;

        double yawRad = Math.toRadians(yaw);

        // ✅ Направление в Minecraft (Z вперёд, X вправо)
        double dirX = -Math.sin(yawRad);
        double dirZ =  Math.cos(yawRad);

        double fovRad = Math.toRadians(fovDeg / 2.0);
        double threshold = Math.cos(fovRad); // угол отсечения

        float maxDistance = radius * 1.6f;

        Map<BotPositionKey, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData b : blocks) {
            blockMap.put(b.toKey(), b);
        }

        int tagged = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > maxDistance || dist < 0.001) continue;

                // Косинус угла между (dx,dz) и направлением взгляда
                double dot = (dx * dirX + dz * dirZ) / dist;
                if (dot < threshold) continue;

                int baseX = (int) Math.floor(botX) + dx;
                int baseZ = (int) Math.floor(botZ) + dz;

                for (int y = minY; y <= maxY; y++) {
                    BotPositionKey key = new BotPositionKey(baseX, y, baseZ);
                    BotBlockData target = blockMap.get(key);
                    if (target != null) {
                        target.addTag("fov:slice");
                        tagged++;
                    }
                }
            }
        }

        return tagged;
    }



    public static void tagFovSliceRemoveAll(List<BotBlockData> blocks) {
        if (blocks == null) return;
        // Очистка FOV
        for (BotBlockData block : blocks) {
            block.getTags().remove("fov:slice");
        }
    }
}
