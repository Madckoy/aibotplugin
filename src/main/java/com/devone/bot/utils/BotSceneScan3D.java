package com.devone.bot.utils;

import com.devone.bot.core.Bot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotSceneScan3D {

    public static BotSceneData scan(Bot bot, int scanRadius, int height) {

        int deltaY = (height - 1) / 2;
        World world = bot.getNPCEntity().getWorld();

        // Центр сканирования
        Location botLoc = bot.getNPCEntity().getLocation();
        int centerX = botLoc.getBlockX();
        int centerY = botLoc.getBlockY();
        int centerZ = botLoc.getBlockZ();

        int minY = centerY - deltaY;
        int maxY = centerY + deltaY;

        List<BotBlockData> scannedBlocks = new ArrayList<>();
        List<BotBlockData> scannedEntities = new ArrayList<>();

        // 1. Сканирование блоков
        for (int y = maxY; y >= minY; y--) {
            for (int x = -scanRadius; x <= scanRadius; x++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {

                    Location loc = new Location(world, centerX + x, y, centerZ + z);
                    Material material = world.getBlockAt(loc).getType();

                    BotBlockData blockData = new BotBlockData();
                    blockData.x = loc.getBlockX();
                    blockData.y = loc.getBlockY();
                    blockData.z = loc.getBlockZ();
                    blockData.type = material.toString();

                    scannedBlocks.add(blockData);
                }
            }
        }

        // 2. Сканирование живых существ
        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity == bot.getNPCEntity() || entity instanceof Player || entity.isDead()) continue;
            if (entity.getLocation().distance(botLoc) > scanRadius) continue;

            Location loc = entity.getLocation();
            String name = entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
            String type = entity.getType().toString();

            BotBlockData blockData = new BotBlockData();
            blockData.x = loc.getBlockX();
            blockData.y = loc.getBlockY();
            blockData.z = loc.getBlockZ();
            blockData.type = name;
            blockData.bot = false;

            scannedEntities.add(blockData);
        }

        // 3. Координаты бота
        BotCoordinate3D botCoords = new BotCoordinate3D(centerX, centerY, centerZ);

        // 4. Сохраняем всё в JSON
        String fileName = BotConstants.PLUGIN_TMP + bot.getId() + "_scene.json";
        BotSceneData sceneData = new BotSceneData(scannedBlocks, scannedEntities, botCoords);

        try {
            BotSceneSaver.saveToJsonFile(fileName, sceneData);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении карты: " + e.getMessage());
        }

        return sceneData;
    }
}
