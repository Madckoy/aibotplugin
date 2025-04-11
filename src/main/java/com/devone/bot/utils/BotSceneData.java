package com.devone.bot.utils;

import java.util.List;

public class BotSceneData {
    public List<BotBlockData> blocks;
    public List<BotBlockData> entities;
    public BotCoordinate3D botLocation;

    public BotSceneData() {}

    public BotSceneData(List<BotBlockData> blocks, List<BotBlockData> entities, BotCoordinate3D botLocation) {
        this.blocks = blocks;
        this.entities = entities;
        this.botLocation = botLocation;
    }
}
