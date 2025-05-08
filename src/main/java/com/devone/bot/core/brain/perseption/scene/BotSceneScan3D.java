package com.devone.bot.core.brain.perseption.scene;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BotSceneScan3D {

    public static BotSceneData scan(Bot bot, double scanRadius, int height) {

        int deltaY = height;
        World world = BotWorldHelper.getWorld();

        // Центр сканирования
        BotPosition botLoc = bot.getNavigator().getPosition();

        double centerX = botLoc.getX();
        double centerY = botLoc.getY();
        double centerZ = botLoc.getZ();

        double minY = centerY - deltaY;
        double maxY = centerY + deltaY;

        List<BotBlockData> scannedBlocks = new ArrayList<>();
        List<BotBlockData> scannedEntities = new ArrayList<>();

        // 1. Сканирование блоков
        for (double y = maxY; y >= minY; y--) {
            for (double x = -scanRadius; x <= scanRadius; x++) {
                for (double z = -scanRadius; z <= scanRadius; z++) {

                    Location loc = new Location(world, centerX + x, y, centerZ + z);
                    Material material = world.getBlockAt(loc).getType();

                    BotBlockData blockData = new BotBlockData();
                    blockData.setPosition(new BotPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                    blockData.setType(material.toString());

                    scannedBlocks.add(blockData);
                }
            }
        }

        // 2. Сканирование живых существ
        Location botLocWorld = BotWorldHelper.botPositionToWorldLocation(botLoc);

        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity == bot.getNPCEntity() || entity instanceof Player || entity.isDead())
                continue;
            if (entity.getLocation().distance(botLocWorld) > scanRadius)
                continue;

            Location loc = entity.getLocation();

            Material standingOn = loc.getBlock().getType();

            // 🚨 Новый фильтр: игнорировать если стоит в воде
            if (standingOn == Material.WATER || standingOn == Material.BUBBLE_COLUMN || standingOn == Material.SEAGRASS
                    || standingOn == Material.KELP) {
                BotLogger.debug("📡", true,
                        bot.getId() + " 🌊 Обнаружен морской моб в воде, игнорируем: " + entity.getName());
                continue;
            }

            String type = entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
            ;

            BotBlockData blockData = new BotBlockData();
            blockData.setPosition(new BotPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            blockData.setType(type);
            blockData.setUUID(entity.getUniqueId());
            scannedEntities.add(blockData);

        }

        // 3. Координаты бота
        float botYaw = BotUtils.getBotYaw(bot);
        float botPitch =  BotUtils.getBotPitch(bot);

        BotPositionSight botCoords = new BotPositionSight(centerX, centerY, centerZ, botYaw, botPitch);
        BotSceneData sceneData = new BotSceneData(scannedBlocks, scannedEntities, botCoords);

        // if (bot.getNavigator().isStuck()) {
        // long currTime = System.currentTimeMillis();
        // 4. Сохраняем всё в JSON если застряли
        // String fileName = BotConstants.PLUGIN_TMP + bot.getId() +
        // "_stuck_scene.json";

        // try {
        // BotSceneSaver.saveToJsonFile(fileName, sceneData);
        // } catch (IOException e) {
        // System.err.println("Ошибка при сохранении карты: " + e.getMessage());
        // }
        // }

        return sceneData;
    }
}
