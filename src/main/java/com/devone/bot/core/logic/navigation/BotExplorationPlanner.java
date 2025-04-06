package com.devone.bot.core.logic.navigation;

import java.util.*;

import com.devone.bot.utils.BotCoordinate3D;

public class BotExplorationPlanner {

    public static List<List<BotCoordinate3D>> findPathsToDistantTargets(
        BotCoordinate3D current,
            List<BotCoordinate3D> reachable) {

        List<BotCoordinate3D> targets = BotAdaptiveTargetSelector.selectAdaptiveSectorTargets(
            reachable,
            new BotCoordinate3D(current.x, current.y, current.z)
        );
        

        List<List<BotCoordinate3D>> result = new ArrayList<>();
        Set<String> reachableSet = new HashSet<>();
        for (BotCoordinate3D p : reachable) {
            reachableSet.add(p.x + "," + p.y + "," + p.z);
        }

        Set<String> visitedTargets = new HashSet<>();

        for (BotCoordinate3D target : targets) {
            if (target.equals(current)) continue;
            if (visitedTargets.contains(target.x + "," + target.y + "," + target.z)) continue;

            List<BotCoordinate3D> path = BotPathfinder.findPath(current, target, reachableSet);
            if (path != null && !path.isEmpty()) {
                result.add(path);
                visitedTargets.add(target.x + "," + target.y + "," + target.z);
            }
        }

        return result;
    }
}
