package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotEntitiesFilter {

    /**
     * Возвращает все сущности, стоящие на навигационной поверхности.
     * Сравнивает XZ, допускает Y в пределах [0, 2] от уровня поверхности.
     */
    public static List<BotBlockData> filter(List<BotBlockData> entities, List<BotBlockData> surface) {
        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData entity : entities) {
            for (BotBlockData block : surface) {
                if (entity.getX() == block.getX() && entity.getZ() == block.getZ()) {
                    int dy = (int)entity.getY() - (int)block.getY();
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
