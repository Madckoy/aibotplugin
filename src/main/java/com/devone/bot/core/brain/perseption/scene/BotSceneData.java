package com.devone.bot.core.brain.perseption.scene;

import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotSceneData {
    public List<BotBlockData> blocks;
    public List<BotBlockData> entities;
    public BotPositionSight bot;

    public BotSceneData() {}

    public BotSceneData(List<BotBlockData> blocks, List<BotBlockData> entities, BotPositionSight botPos) {
        this.blocks = blocks;
        this.entities = entities;
        this.bot = botPos;
    }
}
