package com.devone.aibot.core.logic.tasks.configs;

public class BotMoveTaskConfig extends BotTaskConfig {
    
    private final float speedMultiplier;

    public BotMoveTaskConfig() {
        super(BotMoveTaskConfig.class.getSimpleName());

        this.speedMultiplier = 1.5f; // Значение по умолчанию (обычный шаг)
    }

    public void generateDefaultConfig() {

        config.set("speedMultiplier", 2.5);

        super.generateDefaultConfig();
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}
