package com.devone.aibot.utils;

import org.bukkit.Location;
import java.util.Random;

public class BotNavigationUtil {
    private static final Random random = new Random();

    public static Location getRandomWalkLocation(Location currentLocation, int minRange, int maxRange) {
        int offsetX = random.nextInt(maxRange - minRange + 1) + minRange;
        int offsetZ = random.nextInt(maxRange - minRange + 1) + minRange;
        int offsetY = 0;//random.nextInt(maxRange - minRange + 1) + minRange;

        return currentLocation.clone().add(offsetX * 2,offsetY*2, offsetZ * 2); // Увеличиваем шаг в 2 раза
    }
}
