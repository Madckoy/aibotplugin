package com.devone.bot.core.web.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import java.net.URI;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;


public class BotImageUtils {

    
public static String getSkinFile(UUID botUUID) {
    File skinFolder = new File(BotConstants.PLUGIN_PATH + "/web/skins");
    if (!skinFolder.exists()) skinFolder.mkdirs();

    File skinFile = new File(skinFolder, botUUID + ".png");

    if (skinFile.exists()) return "/skins/" + botUUID + ".png";

    try {
        // Используем URI → URL
        URI uri = new URI("https", "crafatar.com", "/avatars/" + botUUID, "size=32");
        BufferedImage originalImage = ImageIO.read(uri.toURL());

        BufferedImage resizedImage = resizeImage(originalImage, 16, 16);
        ImageIO.write(resizedImage, "png", skinFile);

        BotLogger.debug("✅", true, "Скачан и уменьшен скин для " + botUUID);
    } catch (Exception e) {
        BotLogger.debug("❌", true, "Ошибка скачивания скина: " + e.getMessage());
        return "/skins/default-bot.png";
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
