package com.devone.aibot.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;


public class BotImageUtils {

    
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

}
