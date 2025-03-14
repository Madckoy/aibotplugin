package com.devone.aibot.utils;

import org.bukkit.Location;
import java.util.Random;

public class BotUtils {
    private static final Random random = new Random();

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

    public static String formatLocation(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }

    public static boolean hasReachedTarget(Location current, Location target) {
        double tolerance = 2; // ✅ Добавляем допустимую погрешность

        double distanceSquared = current.distanceSquared(target);
        BotLogger.info("📏 Расстояние до цели (квадрат): " + distanceSquared);

        if (distanceSquared <= tolerance * tolerance) {
            BotLogger.info(" ✅ достиг цели c погрешностью! " + BotUtils.formatLocation(current));
            return true;
        }
        return false;
    }

}
