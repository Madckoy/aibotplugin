package com.devone.bot.core.brain.navigator.tags;


import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;

import com.devone.bot.core.utils.blocks.BotPosition;

public class BotNavigationTagsMaker {

    public static int tagWalkableBlocks(List<BotBlockData> geoBlocks) {
        BotSafeBlockTagger.tagSafeBlocks(geoBlocks);
        int walkable = BotWalkableBlockTagger.tagWalkableBlocks(geoBlocks);      
        return walkable;
    }

    public static int  tagReachableBlocks(BotPosition botPosition,
                                                          List<BotBlockData> geoBlocks,
                                                          double sightFov, int radius, int height) {
          
        BotFovSliceTagger.tagFovSliceAll(geoBlocks, botPosition, sightFov, radius, height);
        int reachable = BotReachableBlockTagger.tagReachableBlocks(geoBlocks, botPosition);
        return reachable;
    }
}
