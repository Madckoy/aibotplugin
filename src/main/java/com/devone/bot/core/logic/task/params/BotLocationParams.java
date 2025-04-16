package com.devone.bot.core.logic.task.params;

import com.devone.bot.utils.blocks.BotLocation;

public class BotLocationParams extends BotTaskParams {

    private BotLocation location = new BotLocation(0, 0, 0);

    public BotLocationParams() {
        // Загружаем из файла и копируем значения
        BotLocationParams loaded = loadOrCreate(BotLocationParams.class);
        this.location = loaded.location;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public BotLocation getCoordinate3D() {
        return location;
    }

    public void setCoordinate3D(BotLocation coord) {
        this.location = coord;
    }
}
