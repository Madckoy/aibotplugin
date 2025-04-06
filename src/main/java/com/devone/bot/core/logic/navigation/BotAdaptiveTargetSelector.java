package com.devone.bot.core.logic.navigation;

import java.util.*;

import com.devone.bot.utils.BotCoordinate3D;

public class BotAdaptiveTargetSelector {

    public static List<BotCoordinate3D> selectAdaptiveSectorTargets(List<BotCoordinate3D> reachable, BotCoordinate3D bot) {
        Map<Integer, BotCoordinate3D> bestInSector = new HashMap<>();
        Map<Integer, Double> maxDistances = new HashMap<>();

        for (BotCoordinate3D point : reachable) {
            int dx = point.x - bot.x;
            int dz = point.z - bot.z;

            if (dx == 0 && dz == 0) continue;

            double angle = Math.atan2(dz, dx); // radians from -π to π
            double degrees = Math.toDegrees(angle);
            if (degrees < 0) degrees += 360.0;

            int sector = (int) Math.floor(degrees / 10.0); // ~36 sectors dynamically

            double distanceSq = dx * dx + dz * dz;

            if (!maxDistances.containsKey(sector) || distanceSq > maxDistances.get(sector)) {
                bestInSector.put(sector, point);
                maxDistances.put(sector, distanceSq);
            }
        }

        return new ArrayList<>(bestInSector.values());
    }
}
