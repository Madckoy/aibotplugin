package com.devone.bot.core.brain.logic.navigator.context;

import com.devone.bot.core.brain.logic.navigator.math.filters.BotAddDummyBlock;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotOnSightFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotSafeBlocksFilter;

import java.util.List;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotReachableSurfaceBuilder;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotWalkableSurfaceBuilder;
import com.devone.bot.core.brain.logic.navigator.math.builder.BotOnSightBuilder;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotEntitiesFilter;
import com.devone.bot.core.brain.logic.navigator.math.filters.BotNavigableFilter;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotNavigationContextMaker {

    public static BotNavigationContext createSceneContext(BotPositionSight botPositionSight,
                                                          List<BotBlockData> geoBlocks,
                                                          List<BotBlockData> bioBlocks,
                                                          double sightFov, int radius, int height) {

        BotNavigationContext context = new BotNavigationContext();
        
        List<BotBlockData> safe = BotSafeBlocksFilter.filter(geoBlocks);

        List<BotBlockData> walkable = BotWalkableSurfaceBuilder.build(safe);
        if (walkable == null || walkable.isEmpty()) walkable = safe;

        List<BotBlockData> navigable = BotNavigableFilter.filter(walkable);
        if (navigable == null || navigable.isEmpty()) navigable = walkable;       

        List<BotBlockData> livingTargets = BotEntitiesFilter.filter(bioBlocks, navigable);  // add only animals on navigable surface

        navigable = BotAddDummyBlock.add(botPositionSight, navigable);                      // add mandatory dummy block to calculate reachable blocks from the current position
        List<BotBlockData> reachable = BotReachableSurfaceBuilder.build(navigable);
        if (reachable == null || reachable.isEmpty()) reachable = navigable;           

        //----------------------------------------------------
        BotBlockData position = botPositionSight.toBlockData();
        BotBlockData current  = position;
        BotBlockData below    = new BotBlockData(position.getX(), position.getY()-1, position.getZ());

        reachable = reachable.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();

        // System.out.println("Reachable: " + reachable.size());    

        navigable = navigable.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();

        // System.out.println("Navigable: " + navigable.size());    

        walkable = walkable.stream()
            .filter(p -> !BlockUtils.isSameBlock(p, current))
            .filter(p -> !BlockUtils.isSameBlock(p, below))
            .toList();
            
        // System.out.println("Walkable: " + walkable.size());
        //-----------------------------------------------------    

        context.safe      = safe;
        context.walkable  = walkable;
        context.navigable = navigable;
        context.reachable = reachable;
        context.entities  = livingTargets;                                                     


        float yaw = botPositionSight.getYaw();
        BotPosition eye = new BotPosition(botPositionSight.getX(), botPositionSight.getY(), botPositionSight.getZ());

        context.viewSector = BotOnSightBuilder.buildViewSectorBlocks(eye, yaw, radius*1.5, 
                                                                               height, sightFov);

        context.reachable = BotOnSightFilter.filter(context.reachable, context.viewSector);
        context.navigable = BotOnSightFilter.filter(context.navigable, context.viewSector);
        context.walkable  = BotOnSightFilter.filter(context.walkable, context.viewSector);
        
        return context;
    }
}
