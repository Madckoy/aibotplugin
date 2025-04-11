package com.devone.bot.utils;

import com.devone.bot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotSceneScan3D {

    public static BotSceneData scan(Bot bot, int scanRadius, int height) {

        int deltaY = (height - 1) / 2;
        World world = BotWorldHelper.getWorld();

        // Центр сканирования
        BotCoordinate3D botLoc = bot.getRuntimeStatus().getCurrentLocation();

        int centerX = botLoc.x;
        int centerY = botLoc.y;
        int centerZ = botLoc.z;

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
        Location botLocWorld = BotWorldHelper.getWorldLocation(botLoc);

        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity == bot.getNPCEntity() || entity instanceof Player || entity.isDead()) continue;
            if (entity.getLocation().distance(botLocWorld) > scanRadius) continue;

            Location loc = entity.getLocation();
            String type = entity.getCustomName() != null ? entity.getCustomName() : entity.getName();;

            BotBlockData blockData = new BotBlockData();
            blockData.x = loc.getBlockX();
            blockData.y = loc.getBlockY();
            blockData.z = loc.getBlockZ();
            blockData.type = type;
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
