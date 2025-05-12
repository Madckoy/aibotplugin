package com.devone.bot.core.utils.blocks;

import java.util.Comparator;
import java.util.List;

public class BotBlockDataSorter {

    public enum Axis {
        X, Y, Z
    }

    public static void sort(List<BotBlockData> list, Axis axis, boolean descending) {
        if (list == null || list.isEmpty())
            return;

        Comparator<BotBlockData> comparator = switch (axis) {
            case X -> Comparator.comparingInt(BotBlockData::getX);
            case Y -> Comparator.comparingInt(BotBlockData::getY);
            case Z -> Comparator.comparingInt(BotBlockData::getZ);
        };

        if (descending) {
            comparator = comparator.reversed();
        }

        list.sort(comparator);
    }

    // 🔥 Удобные шорткаты
    public static void sortByX(List<BotBlockData> list, boolean descending) {
        sort(list, Axis.X, descending);
    }

    public static void sortByY(List<BotBlockData> list, boolean descending) {
        sort(list, Axis.Y, descending);
    }

    public static void sortByZ(List<BotBlockData> list, boolean descending) {
        sort(list, Axis.Z, descending);
    }
}
