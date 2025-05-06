package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

public class BotEntitiesFilter {

    /**
     * Возвращает все сущности, стоящие на навигационной поверхности.
     * Сравнивает XZ, допускает Y в пределах [0, 2] от уровня поверхности.
     */
    public static List<BotBlockData> filter(List<BotBlockData> entities, List<BotBlockData> surface) {
        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData entity : entities) {
            BotPositionKey entityXZ = new BotPositionKey(entity.getX(), 0, entity.getZ());

            for (BotBlockData block : surface) {
                BotPositionKey blockXZ = new BotPositionKey(block.getX(), 0, block.getZ());

                if (entityXZ.equals(blockXZ)) {
                    double dy = entity.getY() - block.getY();
                    if (dy >= 0 && dy <= 2) {
                        result.add(entity);
                        break;
                    }
                }
            }
        }

        return result;
    }
}
