package com.devone.bot.core.bot.blocks;

import org.bukkit.Location;

public class BotLocationHelper {
    public static BotLocation convertFrom(Location loc) {
        return new BotLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());  
    }
}
