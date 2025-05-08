package com.devone.bot.core.utils.blocks;

import java.util.List;
import java.util.stream.Collectors;

public class BlockUtils {

    /**
     * Проверка на полное совпадение координат.
     */
    public static boolean isSamePosition(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return (int) a.getX() == (int) b.getX() && (int) a.getY() == (int) b.getY() && (int) a.getZ() == (int) b.getZ();
    }

    /**
     * Проверка совпадения только по горизонтали (X/Z).
     */
    public static boolean isSameXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return (int) a.getX() == (int) b.getX() && (int) a.getZ() == (int) b.getZ();
    }

    /**
     * Расстояние в 3D-пространстве (с квадратным корнем).
     */
    public static double distance(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        int dx = (int) a.getX() - (int) b.getX();
        int dy = (int) a.getY() - (int) b.getY();
        int dz = (int) a.getZ() - (int) b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Расстояние по XZ-плоскости (без Y).
     */
    public static double distanceXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        int dx = (int) a.getX() - (int) b.getX();
        int dz = (int) a.getZ() - (int) b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Быстрая проверка "в пределах радиуса" без извлечения корня.
     */
    public static boolean isWithinSquaredRadius(BotPosition a, BotPosition b, double radiusSquared) {
        if (a == null || b == null) return false;
        int dx = (int) a.getX() - (int) b.getX();
        int dy = (int) a.getY() - (int) b.getY();
        int dz = (int) a.getZ() - (int) b.getZ();
        int distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= radiusSquared;
    }

    /**
     * Проверка, находится ли блок в пределах манхэттенского расстояния.
     */
    public static boolean isWithinManhattan(BotPosition a, BotPosition b, int maxDistance) {
        if (a == null || b == null) return false;
        int dist = Math.abs((int) a.getX() - (int) b.getX()) + Math.abs((int) a.getY() - (int) b.getY()) + Math.abs((int) a.getZ() - (int) b.getZ());
        return dist <= maxDistance;
    }

    /**
     * Возвращает квадрат расстояния (без Math.sqrt).
     */
    public static double distanceSquared(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        int dx = (int) a.getX() - (int) b.getX();
        int dy = (int) a.getY() - (int) b.getY();
        int dz = (int) a.getZ() - (int) b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Проверяет, равны ли две позиции с точностью по каждой координате.
     */
    public static boolean isNearby(BotPosition a, BotPosition b, double radius) {
        return distance(a, b) <= radius;
    }

    /**
     * Получает направление от точки A к точке B в виде вектора {dx, dy, dz}
     */
    public static int[] directionVector(BotPosition from, BotPosition to) {
        if (from == null || to == null) return new int[] { 0, 0, 0 };
        return new int[] {
            Integer.compare((int) to.getX(), (int) from.getX()),
            Integer.compare((int) to.getY(), (int) from.getY()),
            Integer.compare((int) to.getZ(), (int) from.getZ())
        };
    }


    public static BotPosition fromBlock(BotBlockData block) {
        return new BotPosition(block.getX(), block.getY(), block.getZ());
    }

    public static List<BotPosition> fromBlocks(List<BotBlockData> blocks) {
        return blocks.stream()
            .map(BlockUtils::fromBlock)
            .collect(Collectors.toList());
    }
}
