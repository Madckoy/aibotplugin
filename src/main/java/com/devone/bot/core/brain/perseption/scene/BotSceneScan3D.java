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

public static BotSceneData scan(Bot bot, int radius, int height) {
    World world = BotWorldHelper.getWorld();

    BotPosition botLoc = bot.getNavigator().getPosition();
    int centerY = botLoc.getY(); // уровень ног

    int yMin = centerY - height;
    int yMax = centerY + height + 1; // компенсация вверх

    double centerX = botLoc.getX();
    double centerZ = botLoc.getZ();

    List<BotBlockData> scannedBlocks = new ArrayList<>();
    List<BotBlockData> scannedEntities = new ArrayList<>();

    // 1. Сканирование блоков
    for (int y = yMax; y >= yMin; y--) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location loc = new Location(world, centerX + x, y, centerZ + z);
                Material material = world.getBlockAt(loc).getType();

                BotBlockData blockData = new BotBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                blockData.setType(material.toString());
                scannedBlocks.add(blockData);
            }
        }
    }

    // 2. Сканирование живых существ
    Location botLocWorld = BotWorldHelper.botPositionToWorldLocation(botLoc);
    for (LivingEntity entity : world.getLivingEntities()) {
        if (entity == bot.getNPCEntity() || entity instanceof Player || entity.isDead()) continue;
        if (entity.getLocation().distance(botLocWorld) > radius) continue;

        Location loc = entity.getLocation();
        Material standingOn = loc.getBlock().getType();

        if (standingOn == Material.WATER || standingOn == Material.BUBBLE_COLUMN ||
            standingOn == Material.SEAGRASS || standingOn == Material.KELP) {
            BotLogger.debug("📡", true, bot.getId() + " 🌊 Морской моб в воде: " + entity.getName());
            continue;
        }

        String type = entity.getCustomName() != null ? entity.getCustomName() : entity.getName();

        BotBlockData blockData = new BotBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        blockData.setType(type);
        blockData.setUUID(entity.getUniqueId());
        scannedEntities.add(blockData);
    }

    // 3. Положение бота
    float botYaw = BotUtils.getBotYaw(bot);
    float botPitch = BotUtils.getBotPitch(bot);
    BotPositionSight botCoords = new BotPositionSight(centerX, centerY, centerZ, botYaw, botPitch);

    // 4. Служебная информация
    BotScanInfo info = new BotScanInfo(radius, height);

    return new BotSceneData(scannedBlocks, scannedEntities, botCoords, info);
}

}
