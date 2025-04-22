package com.devone.bot.core.brain.memory.scene;

import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;

public class BotSceneData {
    public List<BotBlockData> blocks;
    public List<BotBlockData> entities;
    public BotLocation bot;

    public BotSceneData() {}

    public BotSceneData(List<BotBlockData> blocks, List<BotBlockData> entities, BotLocation botPos) {
        this.blocks = blocks;
        this.entities = entities;
        this.bot = botPos;
    }
}
