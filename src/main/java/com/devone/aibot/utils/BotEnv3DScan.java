package com.devone.aibot.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.devone.aibot.core.Bot;

public class BotEnv3DScan {

    public static Map<Location, Material> scan3D(Bot bot, int scanRadius, int height) { // Один радиус применяется к X и Z
        
        World world = Bukkit.getWorlds().get(0);

        int centerX = bot.getNPCCurrentLocation().getBlockX();
        int centerY = bot.getNPCCurrentLocation().getBlockY();
        int centerZ = bot.getNPCCurrentLocation().getBlockZ();

        int minHeight = centerY - height; // Нижняя граница  Y (-4 от бота) // Ограничение по глубине Y (-4) // Теперь Y правильно ограничен
        int maxHeight = centerY + height; // Верхняя граница Y (+4 от бота) // Ограничение по глубине Y (+4) // Теперь Y правильно ограничен

        Map<Location, Material> scannedBlocks = new HashMap<>();
        JSONArray blockArray = new JSONArray();

        for (int y = maxHeight; y >= minHeight; y--) { // Сканируем сверху вниз { // Правильная ось Y { // Ограничение по высоте
            for (int x = -scanRadius; x <= scanRadius; x++) { // Радиус по X { // Один радиус X и Z {
                for (int z = -scanRadius; z <= scanRadius; z++) { // Радиус по Z { // Один радиус X и Z {
                    Location loc = new Location(world, centerX + x, y, centerZ + z);
                    Material material = world.getBlockAt(loc).getType();
                    // Исключаем листву и деревья из сканирования

                    // if (material == Material.OAK_LEAVES || material == Material.OAK_LOG || material == Material.SPRUCE_LEAVES || material == Material.SPRUCE_LOG || material == Material.BIRCH_LEAVES || material == Material.BIRCH_LOG || material == Material.JUNGLE_LEAVES || material == Material.JUNGLE_LOG || material == Material.ACACIA_LEAVES || material == Material.ACACIA_LOG || material == Material.DARK_OAK_LEAVES || material == Material.DARK_OAK_LOG) {
                    //    continue;
                    //}

                    scannedBlocks.put(loc, material);

                    JSONObject blockData = new JSONObject();
                    blockData.put("x", centerX + x);
                  
                    if (BotConstants.FLIP_COORDS) {
                        blockData.put("y", centerZ + z);
                        blockData.put("z", y);
                    } else {
                        blockData.put("y", y);
                        blockData.put("z", centerZ + z);
                    }

                    blockData.put("type", material.name());

                    if (x == 0 && y == centerY && z == 0) {
                        blockData.put("bot", true); // Отмечаем позицию бота
                    }

                    blockArray.add(blockData);
                }
            }
        }

        // Запись в JSON файл
        saveScanResultToFile(bot, blockArray);

        return scannedBlocks;
    }

    private static void saveScanResultToFile(Bot bot, JSONArray scanData) {
        //File scanFile = new File(BotConstants.PLUGIN_TMP, bot.getId()+"3d_vision" + System.currentTimeMillis() + ".json"); // Добавляем timestamp
        File scanFile = new File(BotConstants.PLUGIN_TMP, bot.getId()+"vision_log.json"); // Добавляем timestamp

        int centerX = bot.getNPCCurrentLocation().blockX();
        int centerY = bot.getNPCCurrentLocation().blockY();
        int centerZ = bot.getNPCCurrentLocation().blockZ();

        try (FileWriter file = new FileWriter(scanFile)) {
            JSONObject root = new JSONObject();
            if (BotConstants.FLIP_COORDS) {
                root.put("bot_position", new JSONObject(Map.of("x", centerX, "y", centerZ, "z", centerY)));
            } else {
                root.put("bot_position", new JSONObject(Map.of("x", centerX, "y", centerY, "z", centerZ)));
            }
            root.put("blocks", scanData);
            file.write(root.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Ошибка при записи файла сканирования: " + e.getMessage());
        }
    }

    public static JSONArray findEdgeBlocks(Map<Location, Material> scannedBlocks) {
        JSONArray edgeBlocks = new JSONArray();
        for (Location loc : scannedBlocks.keySet()) {
            Material material = scannedBlocks.get(loc);
            //if (material != Material.GRASS_BLOCK && material != Material.SAND) continue;
            boolean isEdge = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    Location neighborLoc = new Location(loc.getWorld(), loc.getBlockX() + dx, loc.getBlockY(), loc.getBlockZ() + dz);
                    if (!scannedBlocks.containsKey(neighborLoc)) {
                        isEdge = true;
                        break;
                    }
                }
                if (isEdge) break;
            }
            if (isEdge) {
                JSONObject blockData = new JSONObject();
                blockData.put("x", loc.getBlockX());
                blockData.put("y", loc.getBlockY());
                blockData.put("z", loc.getBlockZ());
                blockData.put("type", material.name());
                edgeBlocks.add(blockData);
            }
        }
        return edgeBlocks;
    }

    public static Location getRandomEdgeBlock(Map<Location, Material> scannedBlocks) {
        List<Location> edgeLocations = new ArrayList<>();
        for (Location loc : scannedBlocks.keySet()) {
            Material material = scannedBlocks.get(loc);
            //if (material != Material.GRASS_BLOCK && material != Material.SAND) continue;
            boolean isEdge = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    Location neighborLoc = new Location(loc.getWorld(), loc.getBlockX() + dx, loc.getBlockY(), loc.getBlockZ() + dz);
                    if (!scannedBlocks.containsKey(neighborLoc)) {
                        isEdge = true;
                        break;
                    }
                }
                if (isEdge) break;
            }
            if (isEdge) edgeLocations.add(loc);
        }
        if (edgeLocations.isEmpty()) return null;
        return edgeLocations.get(new Random().nextInt(edgeLocations.size()));
    }
}
