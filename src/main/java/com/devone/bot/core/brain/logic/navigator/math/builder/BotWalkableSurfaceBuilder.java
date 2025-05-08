package com.devone.bot.core.brain.logic.navigator.math.builder;

import java.util.*;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

public class BotWalkableSurfaceBuilder {

    public static List<BotBlockData> build(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();
        Map<BotPositionKey, BotBlockData> blockMap = new HashMap<>();

        // Построим map для быстрой навигации по координате
        for (BotBlockData block : blocks) {
            blockMap.put(block.toKey(), block);
        }

        for (BotBlockData block : blocks) {
            if (block.isAir()) continue;

            double x = block.getX();
            double y = block.getY();
            double z = block.getZ();

            BotBlockData above1 = blockMap.get(new BotPositionKey(x, y + 1, z));
            BotBlockData above2 = blockMap.get(new BotPositionKey(x, y + 2, z));

            if (block.isCover()) {
                if (isAir(above1)) {
                    result.add(createFakeBlock(x, y, z, "DUMMY", "walkable:cover"));
                }
            } else {
                if (isAir(above1) && isAir(above2)) {
                    result.add(createFakeBlock(x, y, z, "DUMMY", "walkable:solid"));
                }
            }
        }

        return result;
    }

    private static BotBlockData createFakeBlock(double x, double y, double z, String type, String tag) {
        BotBlockData fake = new BotBlockData();
        fake.setX((int) x);
        fake.setY((int) y);
        fake.setZ((int) z);
        fake.setType(type);
        fake.setTag(tag);
        return fake;
    }

    private static boolean isAir(BotBlockData block) {
        return block != null && block.isAir();
    }
}
