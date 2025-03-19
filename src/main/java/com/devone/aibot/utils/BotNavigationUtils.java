package com.devone.aibot.utils;

import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.core.Bot;


public class BotNavigationUtils {
    
    public static boolean hasReachedTarget(Bot bot, Location target, double tolerance) {
        if (bot.getNPCEntity() == null) return false;
    
        Location current = bot.getNPCEntity().getLocation();
        if (current == null || target == null) return false;
        
        if (!current.getWorld().equals(target.getWorld())) return false;
    
        int cx = current.getBlockX(), cy = current.getBlockY(), cz = current.getBlockZ();
        int tx = target.getBlockX(), ty = target.getBlockY(), tz = target.getBlockZ();
    
        double distanceXZ = Math.sqrt(Math.pow(cx - tx, 2) + Math.pow(cz - tz, 2)); // 🔥 Только XZ
        double yDifference = Math.abs(cy - ty);
 
        BotLogger.trace("✅ " + bot.getId() + " ZX  " + distanceXZ+ "  Y " + yDifference);

        // ✅ Если бот рядом по XZ и высота ±2 блока, считаем, что он дошёл
        if (distanceXZ <= tolerance && yDifference <= tolerance) {
            BotLogger.trace("✅ " + bot.getId() + " Бот достиг цели! " + BotStringUtils.formatLocation(current));
            return true;
        }
    
        // ✅ Дополнительная проверка, если `distanceSquared()` даёт маленькое значение
        double distanceSquared = current.distanceSquared(target);
        if (distanceSquared < tolerance && yDifference <= tolerance) {
            BotLogger.trace("🎯 " + bot.getId() + " Бот достаточно близко (по `distanceSquared()`), завершаем.");
            return true;
        }
    
        return false;
    }
    
    public static boolean isSuitableForNavigation(Location location, Material material) {
        return material.isSolid() && location.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
            && material != Material.LAVA && material != Material.WATER;
    }
}
