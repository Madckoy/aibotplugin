package com.devone.bot.core.logic.navigation;

import java.util.List;
import java.util.Random;

import com.devone.bot.utils.BotCoordinate3D;

public class BotRouteSelector {

    private static final Random RANDOM = new Random();

    /**
     * Выбирает один маршрут для бота из списка валидных маршрутов.
     * На текущем этапе — случайным образом.
     */
    public static List<BotCoordinate3D> choosePath(List<List<BotCoordinate3D>> validPaths) {
        if (validPaths == null || validPaths.isEmpty()) {
            return null;
        }

        int index = RANDOM.nextInt(validPaths.size());
        return validPaths.get(index);
    }
}