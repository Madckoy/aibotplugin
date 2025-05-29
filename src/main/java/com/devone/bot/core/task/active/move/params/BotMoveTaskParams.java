package com.devone.bot.core.task.active.move.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotMoveTaskParams extends BotTaskParams {

    private float speed = 1.0F;
    private boolean autoPickup = true;

    public boolean isAutoPickup() {
        return autoPickup;
    }

    public void setAutoPickup(boolean autoPickup) {
        this.autoPickup = autoPickup;
    }

    public BotMoveTaskParams() {
        // дефолтные значения
        setIcon("🏃🏻‍♂️‍➡️");
        setObjective("Move");
    }

    public BotMoveTaskParams(BotPosition target) {
        this(); // загружаем всё остальное
    }

    public BotMoveTaskParams(BotPosition target, float speed) {
        this(); // загружаем всё остальное
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "BotMoveTaskParams{" +
                ", speed=" + speed +
                '}';
    }
}
