package com.devone.aibot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.devone.aibot.core.Bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockScanner3D {

    public static Map<Location, Material> scanSurroundings(Location center, int scanRadius) {
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        
        Map<Location, Material> scannedBlocks = new HashMap<>();
        StringBuilder scanResult = new StringBuilder("\n🔍 3D-сканер окружения (Радиус: " + scanRadius + ")\n");

        // Перебираем все блоки в указанном радиусе
        for (int y = scanRadius; y >= -scanRadius; y--) { // Начинаем с верхних слоев
            scanResult.append("\n--- Y=").append(centerY + y).append(" ---\n");
            for (int x = -scanRadius; x <= scanRadius; x++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    Location loc = new Location(world, centerX + x, centerY + y, centerZ + z);
                    Material material = world.getBlockAt(loc).getType();
                    scannedBlocks.put(loc, material);

                    String blockCode = material.name().substring(0, Math.min(4, material.name().length()));

                    // Отмечаем позицию бота в матрице
                    if (x == 0 && y == 0 && z == 0) {
                        blockCode = "BOT";
                    }

                    scanResult.append(String.format("[%s] ", blockCode));
                }
                scanResult.append("\n");
            }
        }

        // Логируем результат
        BotLogger.debug(scanResult.toString());

        return scannedBlocks;
    }

    public static void logNearbyEntities(Bot bot) {
        double radius = 5.0; // Увеличенный радиус
        List<Entity> nearbyEntities = bot.getNPCEntity().getNearbyEntities(radius, radius, radius);
    
        if (nearbyEntities.isEmpty()) {
            Bukkit.getLogger().info("[Bot] В радиусе " + radius + " блоков нет НИЧЕГО.");
            return;
        }
    
        Bukkit.getLogger().info("[Bot] В радиусе " + radius + " блоков есть:");
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                ItemStack item = ((Item) entity).getItemStack();
                Bukkit.getLogger().info("  - 🎁 " + item.getAmount() + "x " + item.getType());
            } else {
                Bukkit.getLogger().info("  - 🔹 " + entity.getType() + " (" + entity.getName() + ")");
            }
        }
    }


}
