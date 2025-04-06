package com.devone.bot.core.logic.navigation;

import java.util.List;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class BotNavigator {

    public BotCoordinate3D getNavigableTargetRND(String json) {
        List<BotBlockData> data = null;
        BotGeoDataLoader loader = new BotGeoDataLoader();
        
        try {
            data = loader.loadFromJson(json);
        } catch (Exception e) {
            System.err.println("Error loading JSON data: " + e.getMessage());
            return null;
        }
        if (data == null || data.isEmpty()) {
            System.out.println("No blocks found in the JSON data.");
            return null;
        }


        BotCoordinate3D botPosition = loader.getBotPosition();

        List<BotBlockData> trimmedBlocks = BotVerticalRangeFilter.trimByYRange(data, botPosition.y, 2);

        List<BotCoordinate3D> reachable = BotSafeBlockFilter.extractReachableBlocksFromBot(
            trimmedBlocks, botPosition);


        List<List<BotCoordinate3D>> validPaths = BotExplorationPlanner.findPathsToDistantTargets(
            new BotCoordinate3D(botPosition), reachable);

        List<BotCoordinate3D> selectedPath = BotRouteSelector.choosePath(validPaths);

        if (selectedPath != null && !selectedPath.isEmpty()) {
            return selectedPath.get(selectedPath.size() - 1);
        } else {
            System.out.println("No valid path found.");
            return null;
        }
    }
}