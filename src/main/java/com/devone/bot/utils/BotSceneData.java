package com.devone.bot.utils;

import java.util.List;

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
