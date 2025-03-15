package com.devone.aibot.utils;

import org.bukkit.Location;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

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

    public static String getSkinFile(UUID botUUID) {
        File skinFolder = new File(Constants.PLUGIN_PATH + "/web/skins");
        if (!skinFolder.exists()) skinFolder.mkdirs();
    
        File skinFile = new File(skinFolder, botUUID + ".png");
    
        // ✅ Если файл уже существует — просто возвращаем путь
        if (skinFile.exists()) return "/skins/" + botUUID + ".png";
    
        try {
            // ✅ Скачиваем скин с сервиса Crafatar (официальное API Mojang)
            URL url = new URL("https://crafatar.com/avatars/" + botUUID + "?size=32");
    
            Files.copy(url.openStream(), skinFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    
            BotLogger.info("✅ Скачан скин для " + botUUID);
        } catch (IOException e) {
            BotLogger.warning("⚠ Ошибка скачивания скина: " + e.getMessage());
            return "/skins/default-bot.png"; // ✅ Используем запасной скин
        }
    
        return "/skins/" + botUUID + ".png";
    }
    
    

}
