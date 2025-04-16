package com.devone.bot.core.logic.task.sonar.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;

public class BotSonarTaskParams extends BotTaskParams {

    private int radius = BotConstants.DEFAULT_SCAN_RANGE;
    private int height = BotConstants.DEFAULT_SCAN_RANGE;

    public BotSonarTaskParams() {
        // Устанавливаем значения по умолчанию
        setIcon("𖣠");
        setObjective("Scan Signatures");

        // Загружаем из конфигурационного файла (если он есть)
        BotSonarTaskParams loaded = loadOrCreate(BotSonarTaskParams.class);

        this.radius = loaded.radius;
        this.height = loaded.height;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
