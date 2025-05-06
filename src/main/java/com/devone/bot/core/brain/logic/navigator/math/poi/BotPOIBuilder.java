package com.devone.bot.core.brain.logic.navigator.math.poi;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.*;

public class BotPOIBuilder {

    public enum BotPOIBuildStrategy {
        ADAPTIVE_SECTOR,
        EVEN_DISTRIBUTED
    }

    /**
     * Главный метод: выбирает цели (POI) для навигации.
     * Возвращает помеченные копии блоков.
     */
    public static List<BotBlockData> build(BotPosition bot,
                                           List<BotBlockData> reachable,
                                           BotPOIBuildStrategy strategy,
                                           int sectorCount,
                                           int maxTargets,
                                           boolean preferDistant,
                                           double scanRadius) {

        if (reachable == null || reachable.isEmpty()) return List.of();

        List<BotBlockData> targets;
        switch (strategy) {
            case ADAPTIVE_SECTOR:
                targets = selectAdaptiveSectorTargets(reachable, bot, sectorCount);
                break;
            case EVEN_DISTRIBUTED:
                targets = findEvenlyDistributedTargets(reachable, bot, sectorCount, maxTargets, preferDistant, scanRadius);
                break;
            default:
                return List.of();
        }

        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData original : targets) {
            BotBlockData copy = new BotBlockData(); // клонируем
            BotPosition pos = new BotPosition(original.getX(), original.getY(), original.getZ());
            copy.setPosition(pos);
            copy.setType("POI");
            copy.setTag("poi:end");
            result.add(copy);
        }

        return result;
    }

    private static List<BotBlockData> selectAdaptiveSectorTargets(List<BotBlockData> reachable, BotPosition bot, int sectorCount) {
    
        Map<Integer, BotBlockData> bestInSector = new HashMap<>();
        Map<Integer, Double> maxDistances = new HashMap<>();

        for (BotBlockData point : reachable) {
            double dx = point.getX() - bot.getX();
            double dz = point.getZ() - bot.getZ();
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

    private static List<BotBlockData> findEvenlyDistributedTargets(List<BotBlockData> reachable,
                                                                   BotPosition bot,
                                                                   int sectors,
                                                                   int maxTargetsInput,
                                                                   boolean preferDistant,
                                                                   double scanRadius) {

        if (reachable.isEmpty()) return List.of();

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

        if (sectorMap.size() < maxTargets) {
            List<BotBlockData> remaining = new ArrayList<>();
            for (BotBlockData b : reachable) {
                if (!sectorMap.containsValue(b)) remaining.add(b);
            }

            remaining.sort((a, b1) -> Double.compare(squaredDistance(b1, bot), squaredDistance(a, bot)));
            int needMore = maxTargets - sectorMap.size();
            for (int i = 0; i < remaining.size() && i < needMore; i++) {
                sectorMap.put(sectorMap.size() + 1000, remaining.get(i));
            }
        }

        List<BotBlockData> filtered = filterByDistance(new ArrayList<>(sectorMap.values()), 3.0);
        return filtered.size() > maxTargets ? filtered.subList(0, maxTargets) : filtered;
    }

    private static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, double scanRadius) {
        if (reachable.isEmpty()) return 0;
        double area = Math.PI * scanRadius * scanRadius;
        double densityFactor = 0.8;
        int suggested = (int) Math.round(Math.min(reachable.size(), area * densityFactor));
        return Math.max(1, Math.min(suggested, reachable.size()));
    }

    private static double squaredDistance(BotBlockData a, BotPosition b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private static double squaredDistance(BotBlockData a, BotBlockData b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private static List<BotBlockData> filterByDistance(List<BotBlockData> candidates, double minDist) {
        List<BotBlockData> result = new ArrayList<>();
        for (BotBlockData candidate : candidates) {
            boolean tooClose = false;
            for (BotBlockData existing : result) {
                if (squaredDistance(existing, candidate) < minDist * minDist) {
                    tooClose = true;
                    break;
                }
            }
            if (!tooClose) {
                result.add(candidate);
            }
        }
        return result;
    }
}
