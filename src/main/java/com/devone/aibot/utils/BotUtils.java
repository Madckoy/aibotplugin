package com.devone.aibot.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;


import javax.imageio.ImageIO;


public class BotUtils {
    private static final Random random = new Random();

    
    public static String getSkinFile(UUID botUUID) {
        File skinFolder = new File(BotConstants.PLUGIN_PATH + "/web/skins");
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
    
            BotLogger.info("✅ Скачан и уменьшен скин для " + botUUID);
        } catch (IOException e) {
            BotLogger.error("❌ Ошибка скачивания скина: " + e.getMessage());
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
}
