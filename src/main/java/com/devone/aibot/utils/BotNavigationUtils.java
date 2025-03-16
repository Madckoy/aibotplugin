package com.devone.aibot.utils;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class BotNavigationUtils {
    private static final Random random = new Random();

    public static Location getRandomWalkLocation(Location currentLocation, int minRange, int maxRange) {
        int offsetX = random.nextInt(maxRange - minRange + 1) + minRange;
        int offsetZ = random.nextInt(maxRange - minRange + 1) + minRange;
        int offsetY = 0;//random.nextInt(maxRange - minRange + 1) + minRange;

        return currentLocation.clone().add(offsetX * 1,offsetY*1, offsetZ * 1);
    }

    public static Location findNearestNavigableLocation(Location current, Location target, int radius) {
        World world = target.getWorld();

        // Если цель уже проходимая, возвращаем её
        if (isNavigable(target)) {
            return target;
        }

        // Проверяем блоки вокруг в указанном радиусе
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Location newTarget = target.clone().add(dx, 0, dz);
                if (isNavigable(newTarget)) {
                    return newTarget;
                }
            }
        }

        return null; // Нет доступных точек
    }

    // Проверяем, можно ли пройти через этот блок
    private static boolean isNavigable(Location location) {
        Block block = location.getBlock();
        return block.getType().isAir() || block.getType() == Material.WATER; // Можно доработать
    }

    public static boolean hasReachedTarget(Bot bot, Location target, double tolerance) {

        Location current = bot.getNPCCurrentLocation();

        if (current == null || target == null) {
            BotLogger.error("❌ Ошибка: hasReachedTarget() вызван с null-координатами!");
            return false;
        }

        if (!current.getWorld().equals(target.getWorld())) {
            BotLogger.error("❌ " + bot.getId()+ " Ошибка: hasReachedTarget() вызван для разных миров!");
            return false;
        }

        // Логгируем координаты перед расчетом
        BotLogger.info("📍"  + bot.getId() +  " Текущая позиция: " + BotStringUtils.formatLocation(current));
        BotLogger.info("🎯 " + bot.getId() +  " Цель: " + BotStringUtils.formatLocation(target));

        double distanceSquared = current.distanceSquared(target);

        BotLogger.debug("📏 Квадрат расстояния: " + distanceSquared);

        if (distanceSquared <= tolerance * tolerance) {
            BotLogger.info("✅ " + bot.getId()+ " Бот достиг цели! " + BotStringUtils.formatLocation(current));
            return true;
        }

        return false;
    }

    /**
     * Генерирует случайную точку в заданном радиусе вокруг текущей позиции
     */
    public static Location getRandomNearbyLocation(Location base, int radiusXZ, int radiusY) {
        if (base == null) {
            throw new IllegalArgumentException("Базовая локация не может быть null");
        }

        int xOffset = random.nextInt(radiusXZ * 2 + 1) - radiusXZ;
        int yOffset = random.nextInt(radiusY * 2 + 1) - radiusY;
        int zOffset = random.nextInt(radiusXZ * 2 + 1) - radiusXZ;

        return base.clone().add(xOffset, yOffset, zOffset);
    }

}
