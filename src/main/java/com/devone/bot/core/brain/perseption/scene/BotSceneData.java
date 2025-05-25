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

public BotSceneData clone(boolean cleanUp) {
    BotSceneData copy = new BotSceneData();

    // Клонируем info
    if (this.info != null) {
        copy.info = new BotScanInfo();
        copy.info.setRadius(this.info.getRadius());
        copy.info.setHeight(this.info.getHeight());
    }

    // Клонируем bot позицию
    if (this.bot != null) {
        copy.bot = this.bot.clone(); // предполагается, что у BotPositionSight есть clone()
    }

    // Клонируем блоки
    if (this.blocks != null) {
        copy.blocks = this.blocks.stream()
            .map(original -> {
                BotBlockData cloned = original.clone(); // клонируем
                if(cleanUp) {
                   cloned.getTags().clear();               // удаляем все теги
                }
                return cloned;
            })
            .toList();
    }

    // Клонируем сущности
    if (this.entities != null) {
        copy.entities = this.entities.stream()
            .map(BotBlockData::clone)
            .toList();
    }

    return copy;
}

}
