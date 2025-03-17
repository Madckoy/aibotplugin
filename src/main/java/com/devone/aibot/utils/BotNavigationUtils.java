package com.devone.aibot.utils;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class BotNavigationUtils {
    private static final Random random = new Random();

    
    /**
     * Генерирует случайную точку в заданном радиусе вокруг текущей позиции
     */
    private static Location getRandomLocationV1(Location currentLocation, int radiusXZ, int radiusY) {
        if (currentLocation == null) {
            throw new IllegalArgumentException("Базовая локация не может быть null");
        }

        int xOffset = random.nextInt(radiusXZ * 2 + 1) - radiusXZ;
        int yOffset = random.nextInt(radiusY * 2 + 1) - radiusY;
        int zOffset = random.nextInt(radiusXZ * 2 + 1) - radiusXZ;

        Location newloc =  currentLocation.clone().add(xOffset, yOffset, zOffset);

        
        BotLogger.info("🎲 Random coords genereated to travel from "+BotStringUtils.formatLocation(currentLocation) + " to offset "+BotStringUtils.formatLocation(newloc));
       
        return newloc;
    }

    private static Location getRandomLocationV2(Location currentLocation, int minRange, int maxRange) {

        int offsetX = random.nextInt(maxRange - minRange + 1) + minRange;
        int offsetZ = random.nextInt(maxRange - minRange + 1) + minRange;
        int offsetY = currentLocation.getBlockY();//random.nextInt(maxRange - minRange + 1) + minRange;
 
        Location newloc = currentLocation.clone().add(offsetX, offsetY , offsetZ); 

        BotLogger.info("🎲 Random coords genereated to travel from "+BotStringUtils.formatLocation(currentLocation) + " to offset "+BotStringUtils.formatLocation(newloc));
       
        return newloc;
    }

    // loop until navigation pint is confirmed as vavigable
    public static Location createNavigableLocation(Bot bot, int minRange, int maxRange) {

        long startTime = System.currentTimeMillis();

        Location nav_loc = bot.getNPCCurrentLocation();

        while(true) {

            nav_loc = getRandomLocationV2(bot.getNPCCurrentLocation(), minRange, maxRange);

            if(bot.getNPCNavigator().canNavigateTo(nav_loc)) {
                BotLogger.info("✅ Random coords are navgable: "+BotStringUtils.formatLocation(nav_loc));       
                break;
            } 

            // if not found in 1 minute then return null
            long elapsedTime = System.currentTimeMillis() - startTime;
            if( elapsedTime >= 60000 ) { //one minute
                return null;
            }

        }

        return nav_loc;
    }

    public static boolean hasReachedTarget(Bot bot, Location target, double tolerance) {

        Location current = bot.getNPCCurrentLocation();

        if (current == null || target == null) {
            BotLogger.error("❌ Ошибка: hasReachedTarget() вызван с null-координатами!");
            return false;
        }

        if (!current.getWorld().equals(target.getWorld())) {
            BotLogger.error("❌ " + bot.getId()+ " Ошибка: hasReachedTarget() вызван для разных миров!");
            return false;
        }

        // Логгируем координаты перед расчетом
        BotLogger.info("📍"  + bot.getId() +  " Текущая позиция: " + BotStringUtils.formatLocation(current));
        BotLogger.info("🎯 " + bot.getId() +  " Цель: " + BotStringUtils.formatLocation(target));

        double distanceSquared = current.distanceSquared(target);

        BotLogger.info("📏 Квадрат расстояния: " + distanceSquared);

        if (distanceSquared <= tolerance * tolerance) {
            BotLogger.info("✅ " + bot.getId()+ " Бот достиг цели! " + BotStringUtils.formatLocation(current));
            return true;
        }

        return false;
    }

}
