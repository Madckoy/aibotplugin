package com.devone.aibot.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;

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
        if( loc!=null ) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
        } else {
            return "";
        }
    }

    public static boolean hasReachedTarget(Location current, Location target, double tolerance) {
        if (current == null || target == null) {
            BotLogger.debug("⚠ Ошибка: hasReachedTarget() вызван с null-координатами!");
            return false;
        }
    
        if (!current.getWorld().equals(target.getWorld())) {
            BotLogger.debug("⚠ Ошибка: hasReachedTarget() вызван для разных миров!");
            return false;
        }
    
        // Логируем координаты перед расчетом
        BotLogger.debug("📍 Текущая позиция: " + BotUtils.formatLocation(current));
        BotLogger.debug("🎯 Цель: " + BotUtils.formatLocation(target));
    
        double distanceSquared = current.distanceSquared(target);
    
        BotLogger.debug("📏 Квадрат расстояния: " + distanceSquared);
    
        if (distanceSquared <= tolerance * tolerance) {
            BotLogger.debug("✅ Бот достиг цели! " + BotUtils.formatLocation(current));
            return true;
        }
    
        return false;
    }
    
    public static String getSkinFile(UUID botUUID) {
        File skinFolder = new File(Constants.PLUGIN_PATH + "/web/skins");
        if (!skinFolder.exists()) skinFolder.mkdirs();
    
        File skinFile = new File(skinFolder, botUUID + ".png");
    
        // ✅ Если файл уже существует — возвращаем путь
        if (skinFile.exists()) return "/skins/" + botUUID + ".png";
    
        try {
            // ✅ Скачиваем скин с Crafatar (размер 32x32)
            BufferedImage originalImage = ImageIO.read(new URL("https://crafatar.com/avatars/" + botUUID + "?size=32"));
    
            // ✅ Уменьшаем изображение до 16x16
            BufferedImage resizedImage = resizeImage(originalImage, 16, 16);
    
            // ✅ Сохраняем в кэш
            ImageIO.write(resizedImage, "png", skinFile);
    
            BotLogger.debug("✅ Скачан и уменьшен скин для " + botUUID);
        } catch (IOException e) {
            BotLogger.debug("⚠ Ошибка скачивания скина: " + e.getMessage());
            return "/skins/default-bot.png"; // ✅ Запасной скин
        }
    
        return "/skins/" + botUUID + ".png";
    }
    
    /**
     * Масштабирует изображение до указанного размера
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
    
        return outputImage;
    }
    
    
    public static String formatTime(long milliseconds) {
        long hours   = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    

    public static void sendMessageToPlayer(Player to, String from, String message) {
        Component chatMessage;

        if (from == null) {
            // Сообщение от системы
            chatMessage = Component.text("[System] ", NamedTextColor.YELLOW)
                    .append(Component.text(message, NamedTextColor.WHITE));
        } else if (from.contains("Bot")) { 
            // Сообщение от бота (если в имени есть "Bot")
            chatMessage = Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text(from, NamedTextColor.AQUA)) // Имя бота – голубым
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(Component.text(message, NamedTextColor.WHITE));
        } else {
            // Сообщение от игрока или другого отправителя
            chatMessage = Component.text(from + ": ", NamedTextColor.GREEN)
                    .append(Component.text(message, NamedTextColor.WHITE));
        }

        to.sendMessage(chatMessage);
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

}
