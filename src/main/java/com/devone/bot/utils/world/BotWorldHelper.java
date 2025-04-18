package com.devone.bot.utils.world;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.blocks.BotBlockData;
import com.devone.bot.core.bot.blocks.BotLocation;

public class BotWorldHelper {

    /**
     * Проверяет, является ли текущее время в мире бота ночным.
     * @param bot Бот
     * @return true, если ночь (в Minecraft ночь — это время с 13000 до 23999 включительно)
     */
    public static boolean isNight(Bot bot) {
        if (bot == null || bot.getNPCEntity() == null) return false;

        World world = getWorld();
        long time = world.getTime();

        return time >= 13000 && time <= 23999;
    }

    public static World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static Block getBlockAt(BotLocation location) {
        World world = getWorld();
        if (world == null) {
            return null;
        }
        
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();

        return world.getBlockAt(x, y, z);
    }

    public static Location getWorldLocation(BotLocation coordinate) {
        return getBlockAt(coordinate).getLocation();
    }

    public static BotBlockData getWorldSpawnLocation() {
        
        Location spawnLocation = getWorld().getSpawnLocation();
        BotBlockData blockData = new BotBlockData();
        blockData.setX(spawnLocation.getBlockX());
        blockData.setY(spawnLocation.getBlockY());
        blockData.setZ(spawnLocation.getBlockZ());

        return blockData;
    }

    public static BotBlockData worldBlockToBotBlock(Block block) {
        BotBlockData blockData = new BotBlockData();
        blockData.setX(block.getX());
        blockData.setY(block.getY());
        blockData.setZ(block.getZ());
        blockData.setType(block.getType().toString());
        blockData.setBot(false);

        return blockData;
    }

    public static LivingEntity findLivingEntityByUUID(UUID uuid) {
    for (World world : Bukkit.getWorlds()) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof LivingEntity && entity.getUniqueId().equals(uuid)) {
                return (LivingEntity) entity;
            }
        }
    }
    return null;
}
}
