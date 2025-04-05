package com.devone.aibot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.devone.aibot.core.Bot;

public class Bot3DGeoScan {

    @SuppressWarnings("unchecked")
    public static Map<Location, Material> scan3D(Bot bot, int scanRadius, int height) {

        int deltaY = (height-1) / 2;
        World world = Bukkit.getWorlds().get(0);

        int centerX = bot.getRuntimeStatus().getCurrentLocation().getBlockX();
        int centerY = bot.getRuntimeStatus().getCurrentLocation().getBlockY();
        int centerZ = bot.getRuntimeStatus().getCurrentLocation().getBlockZ();

        int minY = centerY - deltaY;
        int maxY = centerY + deltaY;

        Map<Location, Material> scannedBlocks = new HashMap<>();
        JSONArray blockArray = new JSONArray();

        for (int y = maxY; y >= minY; y--) {
            for (int x = -scanRadius; x <= scanRadius; x++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {

                    Location loc = new Location(world, centerX + x, y, centerZ + z);
                    Material material = world.getBlockAt(loc).getType();

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
                        blockData.put("bot", true);
                    }

                    blockArray.add(blockData);
                }
            }
        }

        saveScanResultToFile(bot, blockArray);
        return scannedBlocks;
    }

    private static void saveScanResultToFile(Bot bot, JSONArray scanData) {
        File scanFile = new File(BotConstants.PLUGIN_TMP, bot.getId() + "_scan_data.json");

        int centerX = bot.getRuntimeStatus().getCurrentLocation().getBlockX();
        int centerY = bot.getRuntimeStatus().getCurrentLocation().getBlockY();
        int centerZ = bot.getRuntimeStatus().getCurrentLocation().getBlockZ();

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
            boolean isEdge = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;
                    Location neighborLoc = new Location(loc.getWorld(), loc.getBlockX() + dx, loc.getBlockY(),
                            loc.getBlockZ() + dz);
                    if (!scannedBlocks.containsKey(neighborLoc)) {
                        isEdge = true;
                        break;
                    }
                }
                if (isEdge)
                    break;
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
            boolean isEdge = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;
                    Location neighborLoc = new Location(loc.getWorld(), loc.getBlockX() + dx, loc.getBlockY(),
                            loc.getBlockZ() + dz);
                    if (!scannedBlocks.containsKey(neighborLoc)) {
                        isEdge = true;
                        break;
                    }
                }
                if (isEdge)
                    break;
            }
            if (isEdge)
                edgeLocations.add(loc);
        }
        if (edgeLocations.isEmpty())
            return null;
        return edgeLocations.get(new Random().nextInt(edgeLocations.size()));
    }

    public static Location getRandomNearbyDestructibleBlock(Map<Location, Material> scannedBlocks,
            Location botLocation) {
        Random random = new Random();
        double minDistance = Double.MAX_VALUE;
        List<Location> closestBlocks = new ArrayList<>();

        int botX = botLocation.getBlockX();
        int botY = botLocation.getBlockY();
        int botZ = botLocation.getBlockZ();

        for (Location loc : scannedBlocks.keySet()) {
            Material material = scannedBlocks.get(loc);
            if (material == Material.AIR)
                continue;

            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            if (y != botY - 1 && y != botY + 2) {
                double distance = Math.sqrt(Math.pow(x - botX, 2) + Math.pow(z - botZ, 2));

                if (distance < minDistance) {
                    minDistance = distance;
                    closestBlocks.clear();
                    closestBlocks.add(loc);
                } else if (distance == minDistance) {
                    closestBlocks.add(loc);
                }
            }
        }

        if (closestBlocks.isEmpty())
            return null;
        return closestBlocks.get(random.nextInt(closestBlocks.size()));
    }
}
