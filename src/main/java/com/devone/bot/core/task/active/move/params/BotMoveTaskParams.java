package com.devone.bot.core.task.active.move.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotMoveTaskParams extends BotTaskParams {

    @JsonIgnore
    private transient BotPosition target = new BotPosition(); 

    private float speed = 1.0F;

    public BotMoveTaskParams() {
        // дефолтные значения
        setIcon("🏃🏻‍♂️‍➡️");
        setObjective("Move");
    }

    public BotMoveTaskParams(BotPosition target) {
        this(); // загружаем всё остальное
        this.target = target; // но вручную задаём координату
    }

    public BotMoveTaskParams(BotPosition target, float speed) {
        this(); // загружаем всё остальное
        this.target = target;
        this.speed = speed;
    }

    public BotPosition getTarget() {
        return target;
    }

    public void setTarget(BotPosition target) {
        this.target = target;
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
                "target=" + target +
                ", speed=" + speed +
                '}';
    }
}
