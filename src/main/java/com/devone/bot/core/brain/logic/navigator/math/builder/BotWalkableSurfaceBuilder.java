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

        // Индексация по позиции
        Map<BotPosition, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData block : blocks) {
            blockMap.put(new BotPosition(block.getX(), block.getY(), block.getZ()), block);
        }

        for (BotBlockData block : blocks) {
            if (block.isAir()) continue;

            BotPosition pos = new BotPosition(block.getX(), block.getY(), block.getZ());

            BotBlockData above1 = blockMap.get(new BotPosition(pos.getX(), pos.getY() + 1, pos.getZ()));
            BotBlockData above2 = blockMap.get(new BotPosition(pos.getX(), pos.getY() + 2, pos.getZ()));

            // Покрытие: только один уровень воздуха сверху
            if (block.isCover()) {
                if (isAirOrBot(above1)) {
                    result.add(createFakeBlock(block, pos.getY(), "walkable:cover"));
                }
            } else {
                // Обычный твердый блок: два воздуха/бота над
                if (isAirOrBot(above1) && isAirOrBot(above2)) {
                    result.add(createFakeBlock(block, pos.getY(), "walkable:solid"));
                }
            }
        }

        return result;
    }

    private static boolean isAirOrBot(BotBlockData block) {
        return block != null && (block.isAir() || block.isBot());
    }

    private static BotBlockData createFakeBlock(BotBlockData base, int y, String notes) {
        BotBlockData fake = base.clone();
        fake.setY(y);
        fake.setType("FAKE_NAV");
        fake.setNotes(notes);
        return fake;
    }
}
