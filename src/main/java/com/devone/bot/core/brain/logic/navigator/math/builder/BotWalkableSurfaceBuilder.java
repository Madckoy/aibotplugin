package com.devone.bot.core.brain.logic.navigator.math.builder;

import java.util.*;

import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

public class BotWalkableSurfaceBuilder {

    public static List<BotBlockData> build(List<BotBlockData> blocks) {
      
        List<BotBlockData> result = new ArrayList<>();
        Map<BotPositionKey, BotBlockData> blockMap = new HashMap<>();

        if(blocks==null) return result;

        // Построим map для быстрой навигации по координате
        for (BotBlockData block : blocks) {
            blockMap.put(block.toKey(), block);
        }

        for (BotBlockData block : blocks) {
            if (BlockMaterialUtils.isAir(block)) continue;

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            BotBlockData below1 = blockMap.get(new BotPositionKey(x, y - 1, z));
            BotBlockData above1 = blockMap.get(new BotPositionKey(x, y + 1, z));
            BotBlockData above2 = blockMap.get(new BotPositionKey(x, y + 2, z));

            if (BlockMaterialUtils.isCover(block)) {
                if (BlockMaterialUtils.isAir(above1)) {
                    result.add(createFakeBlock(x, y, z, block.getType(), "walkable:cover"));
                }
                //and checkm the block below
                if(below1==null) {
                    result.add(createFakeBlock(x, y, z, "DUMMY", "walkable:solid"));
                } else {
                    result.add(createFakeBlock(x, y, z, below1.getType(), "walkable:solid"));
                }

            } else {
                if (BlockMaterialUtils.isAir(above1) && BlockMaterialUtils.isAir(above2)) {
                    result.add(createFakeBlock(x, y, z, block.getType(), "walkable:solid"));
                }
            }
        }

        return result;
    }

    private static BotBlockData createFakeBlock(int x, int y, int z, String type, String tag) {
        BotBlockData fake = new BotBlockData();
        fake.setX(x);
        fake.setY(y);
        fake.setZ(z);
        fake.setType(type);
        fake.setTag(tag);
        return fake;
    }
}
