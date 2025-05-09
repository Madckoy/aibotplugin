package com.devone.bot.core.task.active.sonar.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotSonarTaskParams extends BotTaskParams {

    private double radius = BotConstants.DEFAULT_SCAN_RANGE;
    private int height = BotConstants.DEFAULT_SCAN_DATA_SLICE_HEIGHT;

    public BotSonarTaskParams() {
        super();
        // Устанавливаем значения по умолчанию
        setIcon("📡");
        setObjective("Scan Signatures");
    }

    public double getRadius() {
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
