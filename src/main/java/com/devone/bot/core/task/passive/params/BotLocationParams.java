package com.devone.bot.core.task.passive.params;

import com.devone.bot.core.utils.blocks.BotLocation;

public class BotLocationParams extends BotTaskParams {

    private BotLocation location = new BotLocation(0, 0, 0);

    public BotLocationParams() {
        // Загружаем из файла и копируем значения
        super();
    }

    public BotLocation getLocation() {
        return location;
    }

    public void setLocation(BotLocation coord) {
        this.location = coord;
    }
}
