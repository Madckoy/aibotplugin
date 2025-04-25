package com.devone.bot.core.brain.logic.navigator.finder;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplePathUtils {

    public static List<BotPosition> getSmartNeighbors(BotPosition current, Set<BotPosition> walkable) {
        List<BotPosition> result = new ArrayList<>();
    
        double[][] directions = {
            { 1, 0}, {-1, 0},
            { 0, 1}, { 0, -1}
        };
    
        for (double[] d : directions) {
            double dx = d[0];
            double dz = d[1];
    
            // Пробуем шаги вверх, вниз и по уровню
            for (double dy = -1; dy <= 1; dy++) {
                double x = current.getX() + dx;
                double y = current.getY() + dy;
                double z = current.getZ() + dz;
    
                BotPosition neighbor = new BotPosition(x, y, z);
    
                if (walkable.contains(neighbor)) {
                    result.add(neighbor);
                }
            }
        }
    
        return result;
    }


    public  static Set<BotPosition> toLocationSet(List<BotBlockData> blocks) {
    Set<BotPosition> result = new HashSet<>();
    for (BotBlockData b : blocks) {
        result.add(b.getPosition());
    }
    return result;
}
}
