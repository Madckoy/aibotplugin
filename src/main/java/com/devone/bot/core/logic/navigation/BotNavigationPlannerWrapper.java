package com.devone.bot.core.logic.navigation;


import com.devone.bot.core.logic.navigation.BotExplorationTargetPlanner.Strategy;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNavigableFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNoDangerousFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksVerticalSliceFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksWalkableFilter;
import com.devone.bot.core.logic.navigation.filters.BotEntitiesOnSurfaceFilter;
import com.devone.bot.core.logic.navigation.resolvers.BotReachabilityResolver;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;

import java.util.List;

public class BotNavigationPlannerWrapper {

    /**
     * Выбирает цели разведки на основе достигнутых точек.
     * Если sectorCount == null, будет подобрано автоматически по площади.
     * scanRadius теперь тоже рассчитывается адаптивно.
     */
    public static BotSceneContext getSceneContext(List<BotBlockData> geoBlocks, List<BotBlockData> bioBlocks, BotCoordinate3D botPosition) {

        BotSceneContext context = new BotSceneContext();

        List<BotBlockData> sliced       = BotBlocksVerticalSliceFilter.filter(geoBlocks, botPosition.y, 2);//relative!!!
        if (sliced == null || sliced.isEmpty()) return context;

        List<BotBlockData> safe          = BotBlocksNoDangerousFilter.filter(sliced);

        if (safe == null || safe.isEmpty()) return context;

        List<BotBlockData> walkable      = BotBlocksWalkableFilter.filter(safe);
        if (walkable == null || walkable.isEmpty()) return context;

        List<BotBlockData> navigable     = BotBlocksNavigableFilter.filter(walkable);

        if (navigable == null || navigable.isEmpty()) return context;

        BotBlockData fakeBlockDirt = new BotBlockData();

        fakeBlockDirt.x = botPosition.x;
        fakeBlockDirt.y = botPosition.y-1;
        fakeBlockDirt.z = botPosition.z; 
        fakeBlockDirt.type = "DIRT";
            
        navigable.add(fakeBlockDirt);

        // проверить есть ли мобы на navigable surface
        List<BotBlockData> livingTargets = BotEntitiesOnSurfaceFilter.filter(bioBlocks, navigable);

        List<BotBlockData> reachable     = BotReachabilityResolver.resolve(botPosition, navigable);
        if( reachable == null || reachable.isEmpty()) return null;

        int sectorCount = estimateSectorCountByArea(reachable);

        int scanRadius = estimateSafeScanRadius(botPosition, reachable);

        int maxTargets = estimateAdaptiveMaxTargets(reachable, scanRadius);

        List<BotBlockData> targets = BotExplorationTargetPlanner.selectTargets(
                botPosition,
                reachable,
                Strategy.EVEN_DISTRIBUTED,
                sectorCount,
                maxTargets,
                true,
                scanRadius
        );

        context.walkable  = walkable;
        context.navigable = navigable;
        context.blocks    = reachable;
        context.entities  = livingTargets;

        return context;
    }

    /**
     * Расчёт безопасного радиуса сканирования:
     * среднее между средней и максимальной дистанцией до reachable-точек.
     */
    private static int estimateSafeScanRadius(BotCoordinate3D bot, List<BotBlockData> reachable) {
        if (reachable.isEmpty()) return 2;

        double sum = 0;
        double max = 0;

        for (BotBlockData b : reachable) {
            double dx = b.x - bot.x;
            double dy = b.y - bot.y;
            double dz = b.z - bot.z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            sum += dist;
            if (dist > max) max = dist;
        }

        double avg = sum / reachable.size();

        return Math.max(2, (int) Math.round((avg + max) / 2));
    }

    /**
     * Оценка оптимального количества секторов на основе площади по XZ.
     */
    private static int estimateSectorCountByArea(List<BotBlockData> blocks) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BotBlockData block : blocks) {
            minX = Math.min(minX, block.x);
            maxX = Math.max(maxX, block.x);
            minZ = Math.min(minZ, block.z);
            maxZ = Math.max(maxZ, block.z);
        }

        int area = Math.max(1, (maxX - minX + 1) * (maxZ - minZ + 1));
        int estimated = (int) Math.sqrt(area);
        return Math.max(6, Math.min(32, estimated));
    }
    
    private static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, int scanRadius) {
        if (reachable == null || reachable.isEmpty()) return 0;
    
        int count = reachable.size();
    
        // Коэффициент плотности: сколько целей на 1 блок сканируемого радиуса
        double densityFactor = 0.8; // до 80% можно использовать в малых зонах
    
        // Радиус окружности — => площадь = π * R², но у нас не идеально круглая зона
        double approxArea = Math.PI * scanRadius * scanRadius;
    
        // Цели на 1 сектор площади
        int suggested = (int) Math.round(Math.min(count, approxArea * densityFactor));
    
        // Не меньше 1, не больше count
        return Math.max(1, Math.min(suggested, count));
    }
    
}
