package com.devone.bot.core.logic.task.hand.excavate.params;

import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandExcavateTaskParams extends BotHandTaskParams {

    public BotHandExcavateTaskParams() {
        super(); // загрузка из файла
        // Загружаем параметры из файла или используем дефолтные
        setIcon("⛏");
        setObjective("Excavate");

        BotHandExcavateTaskParams loaded = loadOrCreate(BotHandExcavateTaskParams.class);

        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public BotHandExcavateTaskParams(BotBlockData target) {
        this();
        setTarget(target);
    }

    public BotHandExcavateTaskParams(BotBlockData target, double damage) {
        this();
        setTarget(target);
        setDamage(damage);
    }

    public BotHandExcavateTaskParams(double damage) {
        this();
        setDamage(damage);
    }

    @Override
    public String toString() {
        return "BotHandExcavateTaskParams{" +
                "damage=" + getDamage() +
                ", target=" + getTarget() +
                '}';
    }
}
