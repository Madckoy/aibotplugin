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
     * Расстояние в 3D-пространстве (с сохранением точности).
     */
    public static double distance(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Расстояние по XZ-плоскости (без Y).
     */
    public static double distanceXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Быстрая проверка "в пределах радиуса" без извлечения корня.
     */
    public static boolean isWithinSquaredRadius(BotPosition a, BotPosition b, double radiusSquared) {
        if (a == null || b == null) return false;
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= radiusSquared;
    }

    /**
     * Проверка, находится ли блок в пределах манхэттенского расстояния.
     */
    public static boolean isWithinManhattan(BotPosition a, BotPosition b, double maxDistance) {
        if (a == null || b == null) return false;
        double dist = Math.abs(a.getX() - b.getX())
                    + Math.abs(a.getY() - b.getY())
                    + Math.abs(a.getZ() - b.getZ());
        return dist <= maxDistance;
    }

    /**
     * Возвращает квадрат расстояния (без Math.sqrt).
     */
    public static double distanceSquared(BotPosition a, BotPosition b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Проверяет, находится ли позиция в пределах радиуса.
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
    public static BotPosition findNearestReachable(BotPosition current, List<BotPosition> candidates) {
        return candidates.stream()
            .filter(p -> !isSameBlockUnderfoot(current, p))
            .min(Comparator.comparingDouble(current::distanceTo))
            .orElse(null);
    }

    /**
     * Простая эвристика доступности точки: по расстоянию.
     */
    public static boolean isSoftReachable(BotPosition from, BotPosition to) {
        double dist = from.distanceTo(to);
        return dist > 1.5 && dist < 8.0;
    }

    public static boolean isSameBlockUnderfoot(BotPosition bot, BotPosition target) {
        if (bot == null || target == null) return false;
        int botX = (int) bot.getX();
        int botY = (int) (bot.getY() - 1);
        int botZ = (int) bot.getZ();
    
        return botX == (int) target.getX()
            && botY == (int) target.getY()
            && botZ == (int) target.getZ();
    }
    
}
