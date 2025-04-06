package com.devone.bot.core.logic.navigation;

import java.util.*;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class BotNavigationPointFinder {

    public static List<BotCoordinate3D> findTopmostNavigablePoints(List<BotBlockData> blocks) {
        Map<String, List<BotBlockData>> columnMap = new HashMap<>();
        List<BotCoordinate3D> result = new ArrayList<>();

        for (BotBlockData block : blocks) {
            String key = block.x + "," + block.z;
            columnMap.computeIfAbsent(key, k -> new ArrayList<>()).add(block);
        }

        for (Map.Entry<String, List<BotBlockData>> entry : columnMap.entrySet()) {
            List<BotBlockData> column = entry.getValue();
            boolean foundSafe = false;
            int topY = Integer.MIN_VALUE;
            int safeX = 0, safeZ = 0;

            for (BotBlockData b : column) {
                if (b.isAir()) {
                    foundSafe = true;
                    if (b.y > topY) {
                        topY = b.y;
                        safeX = b.x;
                        safeZ = b.z;
                    }
                }
            }

            if (foundSafe) {
                result.add(new BotCoordinate3D(safeX, topY, safeZ));
            }
        }

        return result;
    }

    public static List<BotCoordinate3D> getAllNavigableAirBlocks(List<BotBlockData> blocks) {
        List<BotCoordinate3D> result = new ArrayList<>();
        for (BotBlockData block : blocks) {
            if (block.isAir()) {
                result.add(new BotCoordinate3D(block.x, block.y, block.z));
            }
        }
        return result;
    }
}