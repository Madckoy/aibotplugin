package com.devone.bot.core.utils.blocks;

import java.util.Comparator;
import java.util.List;

public class BotBlockDataSorter {

    public enum BotBlockDataSorterAxis {
        X, Y, Z
    }

    public static void sort(List<BotPosition> list, BotBlockDataSorterAxis axis, boolean descending) {
        if (list == null || list.isEmpty())
            return;

        Comparator<BotPosition> comparator = switch (axis) {
            case X -> Comparator.comparingDouble(BotPosition::getX);
            case Y -> Comparator.comparingDouble(BotPosition::getY);
            case Z -> Comparator.comparingDouble(BotPosition::getZ);
        };

        if (descending) {
            comparator = comparator.reversed();
        }

        list.sort(comparator);
    }

    // 🔥 Удобные методы шорткаты:
    public static void sortByX(List<BotPosition> list, boolean descending) {
        sort(list, BotBlockDataSorterAxis.X, descending);
    }

    public static void sortByY(List<BotPosition> list, boolean descending) {
        sort(list, BotBlockDataSorterAxis.Y, descending);
    }

    public static void sortByZ(List<BotPosition> list, boolean descending) {
        sort(list, BotBlockDataSorterAxis.Z, descending);
    }
}
