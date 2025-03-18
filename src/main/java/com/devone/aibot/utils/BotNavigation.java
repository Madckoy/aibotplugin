package com.devone.aibot.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.BotTaskMove;

import java.util.*;
import java.util.stream.Collectors;

public class BotNavigation {
    
    private static final Random random = new Random();

    public static void navigateTo(Bot bot, Location target, int scanRadius) {
        BotTaskMove moveTask = new BotTaskMove(bot);
        moveTask.configure(target);
        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
    }

    public static Location getRandomPatrolPoint(Bot bot, int scanRadius) {
        Location currentLocation = bot.getNPCEntity().getLocation();
        Map<Location, Material> scannedBlocks = BotScanEnv.scan3D(currentLocation, scanRadius);
    
        List<Location> validPoints = scannedBlocks.entrySet().stream()
            .filter(entry -> isSuitableForNavigation(entry.getKey(), entry.getValue()))
            .map(Map.Entry::getKey)
            .filter(loc -> loc.distanceSquared(currentLocation) > 4.0) // ✅ Отсекаем слишком близкие точки (дальше 2 блоков)
            .collect(Collectors.toList());
    
        if (validPoints.isEmpty()) {
            BotLogger.debug(bot.getId() + " ⚠️ Не найдено подходящих точек патрулирования, увеличиваю радиус...");
            return getRandomPatrolPoint(bot, Math.min(scanRadius + 5, 30)); // ✅ Увеличиваем радиус до 30 (если нужно)
        }
    
        return validPoints.get(new Random().nextInt(validPoints.size())); // ✅ Теперь выбираем случайную точку
    }
    
    public static boolean hasReachedTarget(Bot bot, Location target, double tolerance) {
        if (bot.getNPCEntity() == null) return false;
    
        Location current = bot.getNPCEntity().getLocation();
        if (current == null || target == null) return false;
        
        if (!current.getWorld().equals(target.getWorld())) return false;
    
        int cx = current.getBlockX(), cy = current.getBlockY(), cz = current.getBlockZ();
        int tx = target.getBlockX(), ty = target.getBlockY(), tz = target.getBlockZ();
    
        double distanceXZ = Math.sqrt(Math.pow(cx - tx, 2) + Math.pow(cz - tz, 2)); // 🔥 Только XZ
        double yDifference = Math.abs(cy - ty);
    
        // ✅ Если бот рядом по XZ и высота ±2 блока, считаем, что он дошёл
        if (distanceXZ <= tolerance && yDifference <= 2) {
            BotLogger.debug("✅ " + bot.getId() + " Бот достиг цели! " + BotStringUtils.formatLocation(current));
            return true;
        }
    
        // ✅ Дополнительная проверка, если `distanceSquared()` даёт маленькое значение
        double distanceSquared = current.distanceSquared(target);
        if (distanceSquared < 4.0 && yDifference <= 2) {
            BotLogger.debug("🎯 " + bot.getId() + " Бот достаточно близко (по `distanceSquared()`), завершаем.");
            return true;
        }
    
        return false;
    }
    

    public static boolean isSuitableForNavigation(Location location, Material material) {
        return material.isSolid() && location.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
            && material != Material.LAVA;
    }
}
