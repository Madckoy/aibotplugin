package com.devone.bot.core.utils.blocks;

public class BlockUtils {

    /**
     * Проверка на полное совпадение координат.
     */
    public static boolean isSamePosition(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    /**
     * Проверка совпадения только по горизонтали (X/Z).
     */
    public static boolean isSameXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return a.getX() == b.getX() && a.getZ() == b.getZ();
    }

    /**
     * Расстояние в 3D-пространстве (с квадратным корнем).
     */
    public static double distance(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Расстояние по XZ-плоскости (без Y).
     */
    public static double distanceXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Быстрая проверка "в пределах радиуса" без извлечения корня.
     */
    public static boolean isWithinSquaredRadius(BotPosition a, BotPosition b, double radiusSquared) {
        if (a == null || b == null) return false;
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        int dz = a.getZ() - b.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= radiusSquared;
    }

    /**
     * Проверка, находится ли блок в пределах манхэттенского расстояния.
     */
    public static boolean isWithinManhattan(BotPosition a, BotPosition b, int maxDistance) {
        if (a == null || b == null) return false;
        int dist = Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
        return dist <= maxDistance;
    }

    /**
     * Возвращает квадрат расстояния (без Math.sqrt).
     */
    public static double distanceSquared(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        int dz = a.getZ() - b.getZ();
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
            Integer.compare(to.getX(), from.getX()),
            Integer.compare(to.getY(), from.getY()),
            Integer.compare(to.getZ(), from.getZ())
        };
    }
}
