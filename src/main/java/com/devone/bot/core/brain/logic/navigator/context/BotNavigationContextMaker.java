package com.devone.bot.core.brain.logic.navigator.context;


import com.devone.bot.core.brain.logic.navigator.math.filters.BotAddDummyBlock;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotBySectorFilter;

import java.util.List;
import com.devone.bot.core.brain.logic.navigator.math.poi.BotPOIBuilder;
import com.devone.bot.core.brain.logic.navigator.math.poi.BotPOIBuilder.BotPOIBuildStrategy;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotReachableSurfaceBuilder;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotWalkableSurfaceBuilder;
import com.devone.bot.core.brain.logic.navigator.math.builder.ViewConeBuilder;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotEntitiesFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotNavigableFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotSafeBlocksFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotVerticalSliceFilter;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotNavigationContextMaker {

    /**
     * Выбирает цели разведки на основе достигнутых точек.
     * Если sectorCount == null, будет подобрано автоматически по площади.
     * scanRadius теперь тоже рассчитывается адаптивно.
     */
    public static BotNavigationContext createSceneContext(BotPositionSight botPositionLook, List<BotBlockData> geoBlocks,
            List<BotBlockData> bioBlocks) {

        BotNavigationContext context = new BotNavigationContext();

        List<BotBlockData> sliced = BotVerticalSliceFilter.filter(geoBlocks, botPositionLook.getY(), 10);// relative!!!

        if (sliced == null || sliced.isEmpty()) {
            sliced = geoBlocks;
        }

        List<BotBlockData> safe = BotSafeBlocksFilter.filter(sliced);

        if (safe == null || safe.isEmpty()) {
            safe = sliced;
        }

        List<BotBlockData> walkable = BotWalkableSurfaceBuilder.build(safe);
        if (walkable == null || walkable.isEmpty()) {
            walkable = safe;
        } 

        List<BotBlockData> navigable = BotNavigableFilter.filter(walkable);

        if (navigable == null || navigable.isEmpty()) {
            navigable = walkable;
        }

        navigable = BotAddDummyBlock.apply(botPositionLook, navigable);

        // проверить есть ли мобы на navigable surface
        List<BotBlockData> livingTargets = BotEntitiesFilter.filter(bioBlocks, navigable);

        List<BotBlockData> reachable = BotReachableSurfaceBuilder.build(navigable);
        if (reachable == null || reachable.isEmpty()) {
            reachable = navigable;
        }

        int sectorCount = estimateSectorCountByArea(reachable);
        double scanRadius  = estimateSafeScanRadius(botPositionLook, reachable);
        int maxTargets  = estimateAdaptiveMaxTargets(reachable, scanRadius);

        List<BotBlockData> poiAll = BotPOIBuilder.build(botPositionLook, 
                reachable,
                BotPOIBuildStrategy.EVEN_DISTRIBUTED,
                sectorCount,
                maxTargets,
                true,
                scanRadius);

        // BotLogger.debug("📜", true,  " POI BLOCKS = " + poi);
        //--------------------------------------------------------------------------
        // Строим debug-путь к одной цели по сетке reachable, а не по самим таргетам
        //
        /* 
        Set<BotPosition> navMesh = SimplePathUtils.toLocationSet(reachable); // 🆕 сетка движения
        BotSimplePathFinder pathfinder = new BotSimplePathFinder(navMesh);

        BotPosition debugLoc = new BotPosition(botPosition);
        debugLoc.setY(botPosition.getY()-1);
        
        List<List<BotBlockData>> debugPaths = BotSimplePathFinder.buildAllDebugPathsV2(
            debugLoc,
            poi,
            pathfinder
        );
        
        context.debugPaths = debugPaths;
        */
        //---------------------------------------------------------------------------
        
        float yaw = botPositionLook.getYaw(); // если есть
        //float pitch = botPositionLook.getPitch(); // если есть

        BotPosition eye = new BotPosition(botPositionLook.getX(), botPositionLook.getY(), botPositionLook.getZ());
        context.viewSector = ViewConeBuilder.buildViewSectorBlocks(eye, yaw, BotConstants.DEFAULT_SCAN_RANGE+5.0, 
                                                                             BotConstants.DEFAULT_SCAN_DATA_SLICE_HEIGHT, 
                                                                             BotConstants.DEFAULT_SIGHT_FOV);
        
        List<BotBlockData> poiOnSight = BotBySectorFilter.filter(poiAll, context.viewSector);

        context.sliced     = sliced;
        context.safe       = safe;               
        context.walkable   = walkable;
        context.navigable  = navigable;
        context.reachable  = reachable;
        context.poiGlobal  = poiAll;
        context.entities   = livingTargets;
        context.poiOnSight = poiOnSight;

        return context;
    }

    /**
     * Расчёт безопасного радиуса сканирования:
     * среднее между средней и максимальной дистанцией до reachable-точек.
     */
    private static int estimateSafeScanRadius(BotPosition bot, List<BotBlockData> reachable) {
        if (reachable.isEmpty())
            return 2;

        double sum = 0;
        double max = 0;

        for (BotBlockData b : reachable) {
            double dx = b.getX() - bot.getX();
            double dy = b.getY() - bot.getY();
            double dz = b.getZ() - bot.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            sum += dist;
            if (dist > max)
                max = dist;
        }

        double avg = sum / reachable.size();

        return Math.max(2, (int) Math.round((avg + max) / 2));
    }

    /**
     * Оценка оптимального количества секторов на основе площади по XZ.
     */
    private static int estimateSectorCountByArea(List<BotBlockData> blocks) {

        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;

        for (BotBlockData block : blocks) {
            minX = Math.min(minX, block.getX());
            maxX = Math.max(maxX, block.getX());
            minZ = Math.min(minZ, block.getZ());
            maxZ = Math.max(maxZ, block.getZ());
        }

        double area = Math.max(1, (maxX - minX + 1) * (maxZ - minZ + 1));
        double estimated = Math.sqrt(area);
        return (int)Math.max(6, Math.min(32, estimated));
    }

    private static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, double scanRadius) {
        if (reachable == null || reachable.isEmpty())
            return 0;

        int count = reachable.size();

        // Коэффициент плотности: сколько целей на 1 блок сканируемого радиуса
        double densityFactor = 0.8; // до 80% можно использовать в малых зонах

        // Радиус окружности — => площадь = π * R², но у нас не идеально круглая зона
        double approxArea = Math.PI * scanRadius * scanRadius;

        // Цели на 1 сектор площади
        int suggested = (int) Math.round(Math.min(count, approxArea * densityFactor));

        // Не меньше 1, не больше count
        return (int)Math.max(1, Math.min(suggested, count));
    }

}
