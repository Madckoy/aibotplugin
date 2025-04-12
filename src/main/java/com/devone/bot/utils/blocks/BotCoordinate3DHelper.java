package com.devone.bot.utils.blocks;

import org.bukkit.Location;

public class BotCoordinate3DHelper {
    public static BotCoordinate3D convertFrom(Location loc) {
        return new BotCoordinate3D(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());  
    }
}
