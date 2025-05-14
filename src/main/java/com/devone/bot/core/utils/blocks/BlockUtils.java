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
        return a.toKey().equals(b.toKey());
    }

    /**
     * Проверка совпадения только по горизонтали (X/Z).
     */
    public static boolean isSameXZ(BotPosition a, BotPosition b) {
        if (a == null || b == null) return false;
        return a.getX() == b.getX() && a.getZ() == b.getZ();
    }

    /**
     * Расстояние в 3D-пространстве (евклидово).
     */
    public static int distance(BotPosition a, BotBlockData b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int dx = (int) Math.floor(a.getX()) - b.getX();
        int dy = (int) Math.floor(a.getY()) - b.getY();
        int dz = (int) Math.floor(a.getZ()) - b.getZ();
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Расстояние по XZ-плоскости (без Y).
     */
    public static int distanceXZ(BotBlockData a, BotBlockData b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return (int) Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Быстрая проверка: в пределах радиуса без корня.
     */
    public static boolean isWithinSquaredRadius(BotPosition a, BotBlockData b, double radiusSquared) {
        if (a == null || b == null) return false;
        int dx = (int) Math.floor(a.getX()) - b.getX();
        int dy = (int) Math.floor(a.getY()) - b.getY();
        int dz = (int) Math.floor(a.getZ()) - b.getZ();
        int distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= radiusSquared;
    }

    /**
     * Манхэттенское расстояние.
     */
    public static boolean isWithinManhattan(BotPosition a, BotBlockData b, double maxDistance) {
        if (a == null || b == null) return false;
        int dist = Math.abs((int) Math.floor(a.getX()) - b.getX())
                 + Math.abs((int) Math.floor(a.getY()) - b.getY())
                 + Math.abs((int) Math.floor(a.getZ()) - b.getZ());
        return dist <= maxDistance;
    }

    /**
     * Квадрат евклидова расстояния.
     */
    public static int distanceSquared(BotPosition a, BotBlockData b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int dx = (int) Math.floor(a.getX()) - b.getX();
        int dy = (int) Math.floor(a.getY()) - b.getY();
        int dz = (int) Math.floor(a.getZ()) - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Проверка, находится ли в пределах радиуса.
     */
    public static boolean isNearby(BotPosition a, BotBlockData b, int radius) {
        return distance(a, b) <= radius;
    }

    /**
     * Вектор направления от from к to.
     */
    public static int[] directionVector(BotPosition from, BotBlockData to) {
        if (from == null || to == null) return new int[] { 0, 0, 0 };
        return new int[] {
            Integer.compare(to.getX(), (int) Math.floor(from.getX())),
            Integer.compare(to.getY(), (int) Math.floor(from.getY())),
            Integer.compare(to.getZ(), (int) Math.floor(from.getZ()))
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
     * Поиск ближайшей позиции из списка (по эвклидову расстоянию).
     */
    public static BotBlockData findNearestReachable(BotBlockData current, List<BotBlockData> candidates) {
        return candidates.stream()
            .filter(p -> !isSameBlockUnderfoot(current, p))
            .min(Comparator.comparingInt(current::distanceTo))
            .orElse(null);
    }

     /**
     * Поиск удаленной позиции из списка (по эвклидову расстоянию).
     */
    public static BotBlockData findFarestReachable(BotBlockData current, List<BotBlockData> candidates) {
        return candidates.stream()
            .filter(p -> !isSameBlockUnderfoot(current, p))
            .max(Comparator.comparingInt(current::distanceTo))
            .orElse(null);
    }

    /**
     * Простая эвристика "достижимости".
     */
    public static boolean isSoftReachable(BotBlockData from, BotBlockData to, int maxDistance) {
        double dist = from.distanceTo(to);
        return dist > 1 && dist <= maxDistance;
    }

    /**
     * Проверка: целевой блок прямо под ботом.
     */
    public static boolean isSameBlockUnderfoot(BotBlockData bot, BotBlockData target) {
        if (bot == null || target == null) return false;
        int botX = bot.getX();
        int botY = bot.getY() - 1;
        int botZ = bot.getZ();
        return botX == target.getX() && botY == target.getY() && botZ == target.getZ();
    }

    /**
     * Проверка полной совпадающей позиции.
     */
    public static boolean isSameBlock(BotBlockData a, BotBlockData b) {
        if (a == null || b == null) return false;
        return a.toKey().equals(b.toKey());
    }

    
    public static boolean isHostileEntity(BotBlockData entity) {
        if (entity == null || entity.getType() == null) return false;
        return true;
    }

}
