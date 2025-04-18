package com.devone.bot.utils.scene;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.world.BotWorldHelper;

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
        BotLocation botLoc = bot.getBrain().getCurrentLocation();

        int centerX = botLoc.getX();
        int centerY = botLoc.getY();
        int centerZ = botLoc.getZ();

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
                    blockData.setX(loc.getBlockX());
                    blockData.setY(loc.getBlockY());
                    blockData.setZ(loc.getBlockZ());
                    blockData.setType(material.toString());

                    scannedBlocks.add(blockData);
                }
            }
        }

        // 2. Сканирование живых существ
        Location botLocWorld = BotWorldHelper.getWorldLocation(botLoc);

        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity == bot.getNPCEntity() || entity instanceof Player || entity.isDead())
                continue;
            if (entity.getLocation().distance(botLocWorld) > scanRadius)
                continue;

            Location loc = entity.getLocation();
            String type = entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
            ;

            BotBlockData blockData = new BotBlockData();
            blockData.setX(loc.getBlockX());
            blockData.setY(loc.getBlockY());
            blockData.setZ(loc.getBlockZ());
            blockData.setType(type);
            blockData.setUUID(entity.getUniqueId());
            blockData.setBot(false);

            scannedEntities.add(blockData);
        }
        
            // 3. Координаты бота
            BotLocation botCoords = new BotLocation(centerX, centerY, centerZ);
            BotSceneData sceneData = new BotSceneData(scannedBlocks, scannedEntities, botCoords);

            if(bot.getState().isStuck()) {
                // long currTime = System.currentTimeMillis(); 
                // 4. Сохраняем всё в JSON если застряли
                String fileName = BotConstants.PLUGIN_TMP + bot.getId() + "_stuck_scene.json";

                try {
                    BotSceneSaver.saveToJsonFile(fileName, sceneData);
                } catch (IOException e) {
                    System.err.println("Ошибка при сохранении карты: " + e.getMessage());
                }
            }

        return sceneData;
    }
}
