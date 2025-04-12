package com.devone.bot.core.logic.navigation.filters;

import com.devone.bot.utils.blocks.BotBlockData;

import java.util.ArrayList;
import java.util.List;

public class BotEntitiesOnSurfaceFilter {

    /**
     * Возвращает все сущности, стоящие на навигационной поверхности.
     * Сравнивает XZ, допускает Y в пределах [0, 2] от уровня поверхности.
     */
    public static List<BotBlockData> filter(List<BotBlockData> entities, List<BotBlockData> surface) {
        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData entity : entities) {
            for (BotBlockData block : surface) {
                if (entity.x == block.x && entity.z == block.z) {
                    int dy = entity.y - block.y;
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
