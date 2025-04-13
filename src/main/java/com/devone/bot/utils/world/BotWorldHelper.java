package com.devone.bot.utils.world;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotWorldHelper {

    public static World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static Block getBlockAt(BotCoordinate3D coordinate) {
        World world = getWorld();
        if (world == null) {
            return null;
        }
        
        int x = coordinate.x;
        int y = coordinate.y;
        int z = coordinate.z;

        return world.getBlockAt(x, y, z);
    }

    public static Location getWorldLocation(BotCoordinate3D coordinate) {
        return getBlockAt(coordinate).getLocation();
    }

    public static BotBlockData getWorldSpawnLocation() {
        
        Location spawnLocation = getWorld().getSpawnLocation();
        BotBlockData blockData = new BotBlockData();
        blockData.x = spawnLocation.getBlockX();
        blockData.y = spawnLocation.getBlockY();
        blockData.z = spawnLocation.getBlockZ();

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
