package com.devone.aibot.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.devone.aibot.core.Bot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotScanEnv {

public static Map<Location, Material> scan3D(Location center, int scanRadius) {
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        BotLogger.trace(" 🟢 " + BotStringUtils.formatLocation(center));

        Map<Location, Material> scannedBlocks = new HashMap<>();
        JSONArray blockArray = new JSONArray();

        for (int y = scanRadius; y >= -scanRadius; y--) { // Начинаем с верхних слоев
            for (int x = -scanRadius; x <= scanRadius; x++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    Location loc = new Location(world, centerX + x, centerY + y, centerZ + z);
                    Material material = world.getBlockAt(loc).getType();
                    scannedBlocks.put(loc, material);

                    JSONObject blockData = new JSONObject();
                    blockData.put("x", centerX + x);
                    blockData.put("y", centerY + y);
                    blockData.put("z", centerZ + z);
                    blockData.put("type", material.name());

                    if (x == 0 && y == 0 && z == 0) {
                        blockData.put("bot", true); // Отмечаем позицию бота
                    }

                    blockArray.add(blockData);
                }
            }
        }

        // Запись в JSON файл
        saveScanResultToFile(blockArray);

        return scannedBlocks;
    }

    private static void saveScanResultToFile(JSONArray scanData) {
        File scanFile = new File(BotConstants.PLUGIN_PATH, "scan_result.json");
        try (FileWriter file = new FileWriter(scanFile)) {
            file.write(scanData.toJSONString());
            file.flush();
        } catch (IOException e) {
            BotLogger.error("Ошибка при записи файла сканирования: " + e.getMessage());
        }
    }

    public static List<Entity> scanNearbyNatural(Bot bot, double radius) {
        return bot.getNPCEntity().getNearbyEntities(radius, radius, radius);
    }

    public static void logScanNatural(Bot bot, double radius) {
        List<Entity> nearbyEntities = BotScanEnv.scanNearbyNatural(bot, radius);
    
        if (nearbyEntities.isEmpty()) {
            BotLogger.trace("💡 " + bot.getId()+" В радиусе " + radius + " блоков нет НИЧЕГО.");
            return;
        }
    
        BotLogger.trace("💡 "+ bot.getId()+" В радиусе " + radius + " блоков есть:");

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                ItemStack item = ((Item) entity).getItemStack();
                BotLogger.trace("🎁 " + bot.getId() +" " + item.getAmount() + "x " + item.getType());
            } else {
                BotLogger.trace("🔹 " + bot.getId() + " " + entity.getType() + " (" + entity.getName() + ")");
            }
        }
    }


}
