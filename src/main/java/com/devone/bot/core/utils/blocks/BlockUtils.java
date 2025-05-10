package com.devone.bot.core.utils.blocks;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlockUtils {

    /**
     * Проверка на полное совпадение координат (по блокам).
     */
    public static boolean isSamePosition(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return (int) a.getX() == (int) b.getX()
            && (int) a.getY() == (int) b.getY()
            && (int) a.getZ() == (int) b.getZ();
    }

    /**
     * Проверка совпадения только по горизонтали (X/Z).
     */
    public static boolean isSameXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return (int) a.getX() == (int) b.getX()
            && (int) a.getZ() == (int) b.getZ();
    }

    /**
     * Расстояние в 3D-пространстве
     */
    public static int distance(BotPosition a, BotBlockData b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int dx = (int) a.getX() - b.getX();
        int dy = (int) a.getY() - b.getY();
        int dz = (int) a.getZ() - b.getZ();
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Расстояние по XZ-плоскости (без Y).
     */
    public static int distanceXZ(BotBlockData a, BotBlockData b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int dx = (int) a.getX() - b.getX();
        int dz = (int) a.getZ() - b.getZ();
        return (int) Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Быстрая проверка "в пределах радиуса" без извлечения корня.
     */
    public static boolean isWithinSquaredRadius(BotPosition a, BotBlockData b, double radiusSquared) {
        if (a == null || b == null) return false;
        int dx = (int) a.getX() - b.getX();
        int dy = (int) a.getY() - b.getY();
        int dz = (int) a.getZ() - b.getZ();
        int distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= radiusSquared;
    }

    /**
     * Проверка, находится ли блок в пределах манхэттенского расстояния.
     */
    public static boolean isWithinManhattan(BotPosition a, BotBlockData b, double maxDistance) {
        if (a == null || b == null) return false;
        double dist = Math.abs(a.getX() - b.getX())
                    + Math.abs(a.getY() - b.getY())
                    + Math.abs(a.getZ() - b.getZ());
        return dist <= maxDistance;
    }

    /**
     * Возвращает квадрат расстояния (без Math.sqrt).
     */
    public static int distanceSquared(BotPosition a, BotBlockData b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int dx = (int) a.getX() - b.getX();
        int dy = (int) a.getY() - b.getY();
        int dz = (int) a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Проверяет, находится ли позиция в пределах радиуса.
     */
    public static boolean isNearby(BotPosition a, BotBlockData b, int radius) {
        return distance(a, b) <= radius;
    }

    /**
     * Получает направление от точки A к точке B в виде вектора {dx, dy, dz}
     */
    public static int[] directionVector(BotPosition from, BotBlockData to) {
        if (from == null || to == null) return new int[] { 0, 0, 0 };
        return new int[] {
            Integer.compare(to.getX(), (int) from.getX()),
            Integer.compare(to.getY(), (int) from.getY()),
            Integer.compare(to.getZ(), (int) from.getZ())
        };
    }

    /**
     * Преобразует BotBlockData в BotPosition.
     */
    public static BotPosition fromBlock(BotBlockData block) {
        return new BotPosition(block.getX(), block.getY(), block.getZ());
    }

    public static List<BotPosition> fromBlocks(List<BotBlockData> blocks) {
        return blocks.stream()
            .map(BlockUtils::fromBlock)
            .collect(Collectors.toList());
    }

    /**
     * Поиск ближайшей позиции из списка.
     */
    public static BotBlockData findNearestReachable(BotBlockData current, List<BotBlockData> candidates) {
        return candidates.stream()
            .filter(p -> !isSameBlockUnderfoot(current, p))
            .min(Comparator.comparingInt(current::distanceTo))
            .orElse(null);
    }

    /**
     * Простая эвристика доступности точки: по расстоянию.
     */
    public static boolean isSoftReachable(BotBlockData from, BotBlockData to) {
        double dist = from.distanceTo(to);
        return dist > 1.5 && dist < 8.0;
    }

    public static boolean isSameBlockUnderfoot(BotBlockData bot, BotBlockData target) {
        if (bot == null || target == null) return false;
        int botX = (int) bot.getX();
        int botY = (int) (bot.getY() - 1);
        int botZ = (int) bot.getZ();
    
        return botX == (int) target.getX()
            && botY == (int) target.getY()
            && botZ == (int) target.getZ();
    }

    /**
     * Проверка, находятся ли два положения на одном и том же блоке (целочисленные координаты X/Y/Z).
     */
    public static boolean isSameBlock(BotBlockData a, BotBlockData b) {
        if (a == null || b == null) return false;
        return (int) a.getX() == (int) b.getX()
            && (int) a.getY() == (int) b.getY()
            && (int) a.getZ() == (int) b.getZ();
    }
    
}
