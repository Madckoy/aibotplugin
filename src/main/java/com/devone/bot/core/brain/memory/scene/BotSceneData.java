package com.devone.bot.core.brain.memory.scene;

import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionLook;

public class BotSceneData {
    public List<BotBlockData> blocks;
    public List<BotBlockData> entities;
    public BotPositionLook bot;

    public BotSceneData() {}

    public BotSceneData(List<BotBlockData> blocks, List<BotBlockData> entities, BotPositionLook botPos) {
        this.blocks = blocks;
        this.entities = entities;
        this.bot = botPos;
    }
}
