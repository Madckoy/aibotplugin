
package com.devone.bot.core.logic.navigation;

import java.util.*;
import java.util.stream.Collectors;

import com.devone.bot.utils.BotCoordinate3D;

public class BotEvenlyDistributedTargetSelector {

    public static List<BotCoordinate3D> findEvenlyDistributedTargets(
            List<BotCoordinate3D> reachable,
            BotCoordinate3D bot,
            int sectors,
            int maxTargets,
            boolean preferDistant,
            int scanRadius) {

        double minRadius = Math.max(2.0, scanRadius * 1); // <-- привязка к радиусу

        Map<Integer, BotCoordinate3D> sectorMap = new HashMap<>();

        for (BotCoordinate3D point : reachable) {
            if (point.x == bot.x && point.z == bot.z) continue;

            double dx = point.x - bot.x;
            double dy = point.y - bot.y;
            double dz = point.z - bot.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            double dist = Math.sqrt(distSq);
            if (dist < minRadius) continue;

            double angle = Math.atan2(dz, dx);
            int sector = (int) ((angle + Math.PI) / (2 * Math.PI) * sectors) % sectors;

            BotCoordinate3D current = sectorMap.get(sector);

            if (current == null ||
                (preferDistant && distSq > squaredDistance(current, bot)) ||
                (!preferDistant && distSq < squaredDistance(current, bot))) {
                sectorMap.put(sector, point);
            }
        }

        return sectorMap.values().stream()
                .sorted(Comparator.comparingDouble(p -> -squaredDistance(p, bot)))
                .limit(maxTargets)
                .collect(Collectors.toList());
    }

    private static double squaredDistance(BotCoordinate3D point, BotCoordinate3D bot) {
        return Math.pow(point.x - bot.x, 2) + Math.pow(point.y - bot.y, 2) + Math.pow(point.z - bot.z, 2);
    }
}
