package com.devone.bot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BotWorldHelper {

    public static World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static String getWorldName() {
        return getWorld().getName();
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
}
