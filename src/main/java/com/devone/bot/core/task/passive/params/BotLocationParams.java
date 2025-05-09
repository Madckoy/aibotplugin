package com.devone.bot.core.task.passive.params;

import com.devone.bot.core.utils.blocks.BotPosition;

public class BotLocationParams extends BotTaskParams {

    private BotPosition position = new BotPosition(0, 0, 0);

    public BotLocationParams() {
        // Загружаем из файла и копируем значения
        super();
    }

    public BotPosition getPosition() {
        return position;
    }

    public void setPosition(BotPosition coord) {
        this.position = coord;
    }
}
