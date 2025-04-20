package com.devone.bot.core.bot.brain.logic.navigator.filters;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotEntitiesOnSurfaceFilter {

    /**
     * Возвращает все сущности, стоящие на навигационной поверхности.
     * Сравнивает XZ, допускает Y в пределах [0, 2] от уровня поверхности.
     */
    public static List<BotBlockData> filter(List<BotBlockData> entities, List<BotBlockData> surface) {
        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData entity : entities) {
            for (BotBlockData block : surface) {
                if (entity.getX() == block.getX() && entity.getZ() == block.getZ()) {
                    int dy = entity.getY() - block.getY();
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
