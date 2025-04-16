package com.devone.bot.core.logic.task.explore.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;

public class BotExploreTaskParams extends BotTaskParams {

    private int scanRadius = BotConstants.DEFAULT_SCAN_RANGE;

    private boolean pickup = true;

    public BotExploreTaskParams() {
        setIcon("🌐");
        setObjective("Explore");
        // Загружаем параметры из файла, если они есть
        BotExploreTaskParams loaded = loadOrCreate(BotExploreTaskParams.class);

        this.scanRadius = loaded.getScanRadius(); // Применяем параметры из загруженного объекта
        this.pickup = loaded.shouldPickup(); // Это значение можно использовать по умолчанию или из файла
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public BotExploreTaskParams(int scanRadius) {
        this();
        this.scanRadius = scanRadius;
    }

    public boolean shouldPickup() {
        return pickup;
    }

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = scanRadius;
    }

    @Override
    public String toString() {
        return "BotExploreTaskParams{" +
                "scanRadius=" + scanRadius +
                ", pickup=" + pickup +
                '}';
    }
}
