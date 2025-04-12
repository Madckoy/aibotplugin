package com.devone.bot.utils.scene;

import java.util.List;

import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotSceneData {
    public List<BotBlockData> blocks;
    public List<BotBlockData> entities;
    public BotCoordinate3D bot;

    public BotSceneData() {}

    public BotSceneData(List<BotBlockData> blocks, List<BotBlockData> entities, BotCoordinate3D botPos) {
        this.blocks = blocks;
        this.entities = entities;
        this.bot = botPos;
    }
}
