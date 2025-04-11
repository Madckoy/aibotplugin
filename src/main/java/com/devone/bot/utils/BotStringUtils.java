package com.devone.bot.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;

public class BotStringUtils {

    public static String formatLocation(BotCoordinate3D coord) {
        if( coord!=null ) {
            return "(" + coord.x + ", " + coord.y + ", " + coord.z + ")";
        } else {
            return "";
        }
    }

    public static String formatLocation(Location loc) {
        if( loc!=null ) {
            return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
        } else {
            return "";
        }
    }

    public static String formatTime(long milliseconds) {
        long hours   = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
