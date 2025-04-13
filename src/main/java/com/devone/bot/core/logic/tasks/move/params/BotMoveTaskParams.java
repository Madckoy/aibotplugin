package com.devone.bot.core.logic.tasks.move.params;

import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotMoveTaskParams extends BotTaskParams { 
    // 🆕 Параметры задачи движения
    public BotCoordinate3D target;
    public float speedMultiplier;

    public BotMoveTaskParams(BotCoordinate3D target, float speedMultiplier) {
        this.target = target;
        this.speedMultiplier = speedMultiplier;
    }
    public BotMoveTaskParams(BotCoordinate3D target) {
        this.target = target;
        this.speedMultiplier = 1.0F;
    }
    public BotMoveTaskParams() {
        this.target = null;
        this.speedMultiplier = 1.0F;
    }
    public BotCoordinate3D getTarget() {
        return target;
    }
    public void setTarget(BotCoordinate3D target) {
        this.target = target;
    }
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }
    @Override
    public String toString() {
        return "BotMoveTaskParams{" +
                "target=" + target +
                ", speedMultiplier=" + speedMultiplier +
                '}';
    }
}
