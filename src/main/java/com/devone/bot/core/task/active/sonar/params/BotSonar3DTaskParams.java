package com.devone.bot.core.task.active.sonar.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotSonar3DTaskParams extends BotTaskParams {

    private int radius = BotConstants.DEFAULT_SCAN_RADIUS;
    private int height = BotConstants.DEFAULT_SCAN_HEIGHT;

    public BotSonar3DTaskParams() {
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
