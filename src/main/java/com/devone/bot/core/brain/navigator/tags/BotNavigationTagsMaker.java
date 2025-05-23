package com.devone.bot.core.brain.navigator.tags;


import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotNavigationTagsMaker {

    public static int tagWalkableBlocks(List<BotBlockData> geoBlocks) {
        BotSafeBlockTagger.tagSafeBlocks(geoBlocks);
        int walkable = BotWalkableBlockTagger.tagWalkableBlocks(geoBlocks);      
        return walkable;
    }

    public static int  tagReachableBlocks(BotPositionSight botPositionSight,
                                                          List<BotBlockData> geoBlocks,
                                                          float sightFov, int radius, int height) {
          
        BotFovSliceTagger.tagFovSliceAll(geoBlocks, botPositionSight, sightFov, radius, height);
        int reachable = BotReachableBlockTagger.tagReachableBlocks(geoBlocks, botPositionSight);
        return reachable;
    }
}
