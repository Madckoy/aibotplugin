package com.devone.bot.core.bot.brain.logic.navigator.scene;
import java.util.List;

import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotSceneContext extends BotSceneData {
    public List<BotBlockData> entities;
    public List<BotBlockData> walkable;
    public List<BotBlockData> navigable;
    public List<BotBlockData> reachable;
    public List<BotBlockData> reachableGoals;
}