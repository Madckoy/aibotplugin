package com.devone.bot.core.logic.tasks.params;

import com.devone.bot.utils.BotCoordinate3D;

public class BotMoveTaskParams extends BotTaskParams { 
    // 🆕 Параметры задачи движения
    public BotCoordinate3D target;
    public double speedMultiplier;

    public BotMoveTaskParams(BotCoordinate3D target, double speedMultiplier) {
        this.target = target;
        this.speedMultiplier = speedMultiplier;
    }
    public BotMoveTaskParams(BotCoordinate3D target) {
        this.target = target;
        this.speedMultiplier = 1.0;
    }
    public BotMoveTaskParams() {
        this.target = null;
        this.speedMultiplier = 1.0;
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
    public void setSpeedMultiplier(double speedMultiplier) {
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
