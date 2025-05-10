package com.devone.bot.core.brain.logic.navigator.context;

import com.devone.bot.core.brain.logic.navigator.math.filters.BotAddDummyBlock;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotOnSightFilter;

import java.util.List;
import com.devone.bot.core.brain.logic.navigator.math.poi.BotPOIBuilder;
import com.devone.bot.core.brain.logic.navigator.math.poi.BotPOIBuilder.BotPOIBuildStrategy;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotReachableSurfaceBuilder;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotWalkableSurfaceBuilder;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotOnSightBuilder;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotEntitiesFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotNavigableFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotSafeBlocksFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotVerticalSliceFilter;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotNavigationContextMaker {

    public static BotNavigationContext createSceneContext(BotPositionSight botPositionSight,
                                                          List<BotBlockData> geoBlocks,
                                                          List<BotBlockData> bioBlocks,
                                                          double sightFov) {

        BotNavigationContext context = new BotNavigationContext();

        List<BotBlockData> sliced = BotVerticalSliceFilter.filter(geoBlocks, botPositionSight.getY(), BotConstants.DEFAULT_SCAN_RANGE);
        if (sliced == null || sliced.isEmpty()) sliced = geoBlocks;

        List<BotBlockData> safe = BotSafeBlocksFilter.filter(sliced);
        if (safe == null || safe.isEmpty()) safe = sliced;

        List<BotBlockData> walkable = BotWalkableSurfaceBuilder.build(safe);
        if (walkable == null || walkable.isEmpty()) walkable = safe;

        List<BotBlockData> navigable = BotNavigableFilter.filter(walkable);
        if (navigable == null || navigable.isEmpty()) navigable = walkable;

        navigable = BotAddDummyBlock.apply(botPositionSight, navigable);

        List<BotBlockData> livingTargets = BotEntitiesFilter.filter(bioBlocks, navigable);

        List<BotBlockData> reachable = BotReachableSurfaceBuilder.build(navigable);
        if (reachable == null || reachable.isEmpty()) reachable = navigable;

        int sectorCount = estimateSectorCountByArea(reachable);
        double scanRadius = estimateSafeScanRadius(botPositionSight, reachable);
        int maxTargets = estimateAdaptiveMaxTargets(reachable, scanRadius);

        List<BotBlockData> poi = BotPOIBuilder.build(botPositionSight,
                                                     reachable,
                                                     BotPOIBuildStrategy.EVEN_DISTRIBUTED,
                                                     sectorCount,
                                                     maxTargets,
                                                     true,
                                                     scanRadius);

        context.sliced    = sliced;
        context.safe      = safe;
        context.walkable  = walkable;
        context.navigable = navigable;
        context.reachable = reachable;
        context.poi       = poi;
        context.entities  = livingTargets;                                                     

        BotBlockData current = new BotBlockData(botPositionSight.getX(), botPositionSight.getY(), botPositionSight.getZ());
        BotBlockData below = new BotBlockData(current.getX(), current.getY() - 1, current.getZ());

        context.poi = context.poi.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();

        context.reachable = context.reachable.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();

        context.navigable = context.navigable.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();

        context.walkable = context.walkable.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();                                                     

        float yaw = botPositionSight.getYaw();
        BotPosition eye = new BotPosition(botPositionSight.getX(), botPositionSight.getY(), botPositionSight.getZ());

        context.viewSector = BotOnSightBuilder.buildViewSectorBlocks(eye, yaw, BotConstants.DEFAULT_SCAN_RANGE+(BotConstants.DEFAULT_SCAN_RANGE/2), 
                                                                               BotConstants.DEFAULT_SCAN_DATA_SLICE_HEIGHT, sightFov);
        if (context.viewSector.isEmpty()) {
            context.viewSector.add(new BotBlockData((int) botPositionSight.getX(),
                                                    (int) botPositionSight.getY(),
                                                    (int) botPositionSight.getZ()));
        }

        context.poi = BotOnSightFilter.filter(poi, context.viewSector);
        if (context.poi.isEmpty()) {
            context.poi.add(new BotBlockData((int) botPositionSight.getX(),
                                             (int) botPositionSight.getY(),
                                             (int) botPositionSight.getZ()));
        }

        context.reachable = BotOnSightFilter.filter(context.reachable, context.viewSector);
        context.navigable = BotOnSightFilter.filter(context.navigable, context.viewSector);
        context.walkable  = BotOnSightFilter.filter(context.walkable, context.viewSector);

        /* 
        // 🔧 Переделано на toKeySet()
        Set<BotPositionKey> navMesh = SimplePathUtils.toKeySet(reachable);
        BotSimplePathFinder pathfinder = new BotSimplePathFinder(navMesh);

         
        BotPosition debugLoc = new BotPosition(botPositionSight);
        debugLoc.setY(botPositionSight.getY() - 1);

        List<List<BotBlockData>> debugPaths = BotSimplePathFinder.buildAllDebugPathsV2(
            debugLoc,
            context.poiOnSight,
            pathfinder
        );

        if (debugPaths.isEmpty()) {
            List<BotBlockData> fallback = new ArrayList<>();
            fallback.add(new BotBlockData((int) botPositionSight.getX(),
                                          (int) botPositionSight.getY(),
                                          (int) botPositionSight.getZ()));
            debugPaths.add(fallback);
        }

        //context.debugPaths = debugPaths;
        */
        return context;
    }

    private static double estimateSafeScanRadius(BotPosition bot, List<BotBlockData> reachable) {
        if (reachable.isEmpty()) return 2;

        double sum = 0;
        double max = 0;

        for (BotBlockData b : reachable) {
            double dx = b.getX() - bot.getX();
            double dy = b.getY() - bot.getY();
            double dz = b.getZ() - bot.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            sum += dist;
            max = Math.max(max, dist);
        }

        double avg = sum / reachable.size();
        return Math.max(2, (int) Math.round((avg + max) / 2));
    }

    private static int estimateSectorCountByArea(List<BotBlockData> blocks) {
        double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        double minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BotBlockData block : blocks) {
            minX = Math.min(minX, block.getX());
            maxX = Math.max(maxX, block.getX());
            minZ = Math.min(minZ, block.getZ());
            maxZ = Math.max(maxZ, block.getZ());
        }

        double area = Math.max(1, (maxX - minX + 1) * (maxZ - minZ + 1));
        double estimated = Math.sqrt(area);
        return (int) Math.max(6, Math.min(32, estimated));
    }

    private static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, double scanRadius) {
        if (reachable == null || reachable.isEmpty()) return 0;

        double densityFactor = 0.8;
        double approxArea = Math.PI * scanRadius * scanRadius;
        int suggested = (int) Math.round(Math.min(reachable.size(), approxArea * densityFactor));

        return Math.max(1, Math.min(suggested, reachable.size()));
    }
}
