package com.devone.bot.core.brain.perseption.scene;

import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotSceneData {
    public BotScanInfo info;
    public BotPositionSight bot;
    public List<BotBlockData> blocks;
    public List<BotBlockData> entities;
    
    public BotSceneData() {}

    public BotSceneData(BotScanInfo info, BotPositionSight botPos, List<BotBlockData> blocks, List<BotBlockData> entities) {
        this.info = info;
        this.bot = botPos;
        this.blocks = blocks;
        this.entities = entities;

    }
}
