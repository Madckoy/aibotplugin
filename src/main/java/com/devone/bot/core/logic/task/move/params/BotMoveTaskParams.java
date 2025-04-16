package com.devone.bot.core.logic.task.move.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.blocks.BotLocation;

public class BotMoveTaskParams extends BotTaskParams {

    private BotLocation target = new BotLocation(); // по умолчанию (0,0,0)
    private float speed = 1.0F;

    public BotMoveTaskParams() {
        // дефолтные значения
        setIcon("🏃🏻‍♂️‍➡️");
        setObjective("Move");

        // подгружаем из файла (если есть)
        BotMoveTaskParams loaded = loadOrCreate(BotMoveTaskParams.class);
        this.target = loaded.target;
        this.speed = loaded.speed;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public BotMoveTaskParams(BotLocation target) {
        this(); // загружаем всё остальное
        this.target = target; // но вручную задаём координату
    }

    public BotMoveTaskParams(BotLocation target, float speed) {
        this(); // загружаем всё остальное
        this.target = target;
        this.speed = speed;
    }

    public BotLocation getTarget() {
        return target;
    }

    public void setTarget(BotLocation target) {
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
