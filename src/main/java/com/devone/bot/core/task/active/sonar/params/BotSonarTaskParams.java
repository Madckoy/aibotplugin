package com.devone.bot.core.task.active.sonar.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotSonarTaskParams extends BotTaskParams {

    private int radius = BotConstants.DEFAULT_SCAN_RANGE;
    private int height = BotConstants.DEFAULT_SCAN_RANGE;

    public BotSonarTaskParams() {
        super();
        // Устанавливаем значения по умолчанию
        setIcon("📡");
        setObjective("Scan Signatures");
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
