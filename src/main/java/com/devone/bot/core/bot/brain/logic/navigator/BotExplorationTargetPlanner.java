
/**
 * BotExplorationTargetPlanner.java
 *
 * Strategic target planning utility for Minecraft bot navigation.
 * Supports adaptive and evenly distributed exploration strategies.
 *
 * @author ChatGPT
 * @version 1.0
 * @since 2025-04-07
 * @generated by ChatGPT on request of Madckoy
 */

package com.devone.bot.core.bot.brain.logic.navigator;

import java.util.*;
 import java.util.stream.Collectors;

import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
 
 public class BotExplorationTargetPlanner {
 
     public enum Strategy {
         ADAPTIVE_SECTOR,
         EVEN_DISTRIBUTED
     }
 
     public static List<BotBlockData> selectTargets(BotLocation bot,
                                                    List<BotBlockData> reachable,
                                                    Strategy strategy,
                                                    int sectorCount,
                                                    int maxTargets,
                                                    boolean preferDistant,
                                                    int scanRadius) {
 
         if (reachable == null || reachable.isEmpty()) return null;
 
         switch (strategy) {
             case ADAPTIVE_SECTOR:
                 return selectAdaptiveSectorTargets(reachable, bot, sectorCount);
             case EVEN_DISTRIBUTED:
                 return findEvenlyDistributedTargets(reachable, bot, sectorCount, maxTargets, preferDistant, scanRadius);
             default:
                 return null;
         }
     }
 
     private static List<BotBlockData> selectAdaptiveSectorTargets(List<BotBlockData> reachable, BotLocation bot, int sectorCount) {
         Map<Integer, BotBlockData> bestInSector = new HashMap<>();
         Map<Integer, Double> maxDistances = new HashMap<>();
 
         for (BotBlockData point : reachable) {
             int dx = point.getX() - bot.getX();
             int dz = point.getZ() - bot.getZ();
 
             if (dx == 0 && dz == 0) continue;
 
             double angle = Math.atan2(dz, dx);
             int sector = (int) ((angle + Math.PI) / (2 * Math.PI) * sectorCount) % sectorCount;
 
             double distanceSq = dx * dx + dz * dz;
 
             if (!maxDistances.containsKey(sector) || distanceSq > maxDistances.get(sector)) {
                 bestInSector.put(sector, point);
                 maxDistances.put(sector, distanceSq);
             }
         }
 
         return new ArrayList<>(bestInSector.values());
     }
 
     private static List<BotBlockData> findEvenlyDistributedTargets(
        List<BotBlockData> reachable,
        BotLocation bot,
        int sectors,
        int maxTargetsInput,
        boolean preferDistant,
        int scanRadius) {

    if (reachable == null || reachable.isEmpty()) return List.of();

    // 📐 Адаптивный maxTargets
    int maxTargets = estimateAdaptiveMaxTargets(reachable, scanRadius);
    if (maxTargets == 0) return List.of();

    double minRadius = (scanRadius > 3) ? scanRadius * 0.5 : 0.0;
    Map<Integer, BotBlockData> sectorMap = new HashMap<>();

    for (BotBlockData point : reachable) {
        if (point.getX() == bot.getX() && point.getZ() == bot.getZ()) continue;

        double dx = point.getX() - bot.getX();
        double dy = point.getY() - bot.getY();
        double dz = point.getZ() - bot.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        double dist = Math.sqrt(distSq);

        if (dist < minRadius) continue;

        double angle = Math.atan2(dz, dx);
        int sector = (int) ((angle + Math.PI) / (2 * Math.PI) * sectors) % sectors;

        BotBlockData current = sectorMap.get(sector);

        if (current == null ||
                (preferDistant && distSq > squaredDistance(current, bot)) ||
                (!preferDistant && distSq < squaredDistance(current, bot))) {
            sectorMap.put(sector, point);
        }
    }

    // 🧱 Fallback: добираем самые удалённые
    if (sectorMap.size() < maxTargets) {
        reachable.stream()
                .filter(p -> !sectorMap.containsValue(p))
                .sorted(Comparator.comparingDouble(p -> -squaredDistance(p, bot)))
                .limit(maxTargets - sectorMap.size())
                .forEach(p -> sectorMap.put(sectorMap.size() + 1000, p));
    }

    // 🎯 Фильтрация по минимальному расстоянию между целями
    List<BotBlockData> filtered = filterByDistance(
            sectorMap.values().stream()
                    .sorted(Comparator.comparingDouble(p -> -squaredDistance(p, bot)))
                    .collect(Collectors.toList()),
            3.0  // минимальная дистанция между целями
    );

    return filtered.stream()
            .limit(maxTargets)
            .collect(Collectors.toList());
}



    private static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, int scanRadius) {
        if (reachable == null || reachable.isEmpty()) return 0;
    
        double area = Math.PI * scanRadius * scanRadius;
        double densityFactor = 0.8;
    
        int suggested = (int) Math.round(Math.min(reachable.size(), area * densityFactor));
        return Math.max(1, Math.min(suggested, reachable.size()));
    }
    
    private static double squaredDistance(BotBlockData a, BotLocation b) {
        return Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2) + Math.pow(a.getZ() - b.getZ(), 2);
    }
    
    private static List<BotBlockData> filterByDistance(List<BotBlockData> candidates, double minDist) {
        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData candidate : candidates) {
            boolean tooClose = result.stream()
                    .anyMatch(p -> squaredDistance(p, candidate) < minDist * minDist);
            if (!tooClose) {
                result.add(candidate);
            }
        }
        return result;
    }
 }
 