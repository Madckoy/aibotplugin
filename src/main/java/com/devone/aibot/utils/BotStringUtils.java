package com.devone.aibot.utils;

import org.bukkit.Location;

import java.util.concurrent.TimeUnit;

public class BotStringUtils {

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
