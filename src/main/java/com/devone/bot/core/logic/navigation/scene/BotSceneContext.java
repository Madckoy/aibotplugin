package com.devone.bot.core.logic.navigation.scene;
import java.util.List;

import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.scene.BotSceneData;

public class BotSceneContext extends BotSceneData {
    public List<BotBlockData> walkable;
    public List<BotBlockData> navigable;
    public List<BotBlockData> reachable;
    public List<BotBlockData> reachableGoals;
}