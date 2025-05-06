package com.devone.bot.core.brain.logic.navigator.math.builder;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.ArrayList;
import java.util.List;

public class ViewConeBuilder {


    public static List<BotBlockData> buildViewSectorBlocks(
        BotPosition eyePos, float yaw,
        double radius, int height, double fovDegrees
    ) {
        List<BotBlockData> sector = new ArrayList<>();

        double[] viewDir = yawToDirection(yaw); // 2D-направление
        double fovRadians = Math.toRadians(fovDegrees);
        double cosThreshold = Math.cos(fovRadians / 2.0);

        // Строим 2D-сектор в XZ
        List<int[]> baseLayer = new ArrayList<>();
        baseLayer.add(new int[]{0, 0});

        for (int dx = -(int) radius; dx <= (int) radius; dx++) {
            for (int dz = -(int) radius; dz <= (int) radius; dz++) {
                double distanceSq = dx * dx + dz * dz;
                if (distanceSq > radius * radius) continue;

                double[] toBlock = normalize(dx, 0, dz);
                double dot = dotProduct(viewDir, toBlock);

                if (dot >= cosThreshold) {
                    baseLayer.add(new int[]{dx, dz});
                }
            }
        }

        // Центрированный eyePos — для симметрии
        double baseX = Math.round(eyePos.getX());
        double baseY = Math.round(eyePos.getY());
        double baseZ = Math.round(eyePos.getZ());

        // Экструдируем сектор вверх и вниз
        for (int dy = -height; dy <= height; dy++) {
            for (int[] offset : baseLayer) {
                int dx = offset[0];
                int dz = offset[1];

                double x = baseX + dx;
                double y = baseY + dy;
                double z = baseZ + dz;

                BotBlockData block = new BotBlockData(x, y, z);
                block.setType("VIEW_CONE");
                sector.add(block);
            }
        }

        // Добавляем блок центра обзора
        BotBlockData center = new BotBlockData(baseX, baseY, baseZ);
        center.setType("VIEW_CONE");
        sector.add(center);

        return sector;
    }

    private static double[] yawToDirection(float yaw) {
        double yawRad = Math.toRadians(-yaw);
        double x = Math.sin(yawRad);
        double z = Math.cos(yawRad);
        return normalize(x, 0, z);
    }
    
    private static double[] normalize(double x, double y, double z) {
        double len = Math.sqrt(x * x + y * y + z * z);
        return len == 0 ? new double[]{0, 0, 0} : new double[]{x / len, y / len, z / len};
    }
    
    private static double dotProduct(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
    

}

