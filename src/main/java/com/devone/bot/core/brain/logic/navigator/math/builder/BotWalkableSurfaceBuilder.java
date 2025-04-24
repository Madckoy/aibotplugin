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
        List<BotBlockData> result = new ArrayList<>(blocks);
    
        for (BotBlockData block : blocks) {
            if (block.isAir()) continue;
    
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
    
            BotBlockData above1 = findBlockAt(blocks, x, y + 1, z);
            BotBlockData above2 = findBlockAt(blocks, x, y + 2, z);
    
            if (block.isCover()) {
                if (isAirOrBot(above1)) {
                    result.add(createFakeBlock(block, y, "DUMMY", "walkable:cover"));
                }
            } else {
                if (isAirOrBot(above1) && isAirOrBot(above2)) {
                    result.add(createFakeBlock(block, y, "DUMMY", "walkable:solid"));
                }
            }
        }
    
        return result;
    }
    
    private static BotBlockData findBlockAt(List<BotBlockData> blocks, int x, int y, int z) {
        for (BotBlockData b : blocks) {
            if (b.getX() == x && b.getY() == y && b.getZ() == z) {
                return b;
            }
        }
        return null;
    }

    private static boolean isAirOrBot(BotBlockData block) {
        return block != null && (block.isAir() || block.isBot());
    }

    private static BotBlockData createFakeBlock(BotBlockData base, int y, String type, String notes) {
        BotBlockData fake = base.clone();
        fake.setY(y);
        fake.setType(type);
        fake.setNotes(notes);
        return fake;
    }
}
