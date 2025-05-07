package com.devone.bot.core.brain.logic.navigator.context;

import java.util.List;

import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotNavigationContext extends BotSceneData {
    public List<BotBlockData> sliced;
    public List<BotBlockData> safe;
    public List<BotBlockData> walkable;
    public List<BotBlockData> navigable;
    public List<BotBlockData> reachable;
    public List<BotBlockData> poiGlobal;
    public List<BotBlockData> entities;
    public List<BotBlockData> viewSector;
    public List<BotBlockData> poiOnSight;


    //public List<BotBlockData> debugPath;
    //public List<List<BotBlockData>> debugPaths; // 🔥 новый, мульти-пути
}