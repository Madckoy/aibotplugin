package com.devone.bot.core.brain.logic.navigator.math.builder;

import java.util.*;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotWalkableSurfaceBuilder {

    /**
     * Строит поверхность из фейковых блоков, пригодных для навигации.
     * Возвращает только искусственные блоки, с пометками (notes), исключая воздух.
     */
    public static List<BotBlockData> build(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();
    
        for (BotBlockData block : blocks) {
            if (block.isAir()) continue;
    
            double x = block.getX();
            double y = block.getY();
            double z = block.getZ();
    
            BotBlockData above1 = findBlockAt(blocks, x, y + 1, z);
            BotBlockData above2 = findBlockAt(blocks, x, y + 2, z);
    
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
        fake.setPosition(new BotPosition(x, y, z));
        fake.setType(type);
        fake.setTag(tag);
        return fake;
    }
        
    private static BotBlockData findBlockAt(List<BotBlockData> blocks, double x, double y, double z) {
        for (BotBlockData b : blocks) {
            if ((int)b.getX() == (int)x && (int)b.getY() == (int)y && (int)b.getZ() == (int)z) {
                return b;
            }
        }
        return null;
    }

    private static boolean isAir(BotBlockData block) {
        return block != null && (block.isAir() );
    }

}
