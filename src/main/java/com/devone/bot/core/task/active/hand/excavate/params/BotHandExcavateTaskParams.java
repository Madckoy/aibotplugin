package com.devone.bot.core.task.active.hand.excavate.params;

import com.devone.bot.core.task.active.hand.params.BotHandTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotHandExcavateTaskParams extends BotHandTaskParams {

    public BotHandExcavateTaskParams() {
        super();
        setIcon("⛏");
        setObjective("Excavate");
    }

    public BotHandExcavateTaskParams(BotBlockData target) {
        this();
         }

    public BotHandExcavateTaskParams(BotBlockData target, double damage) {
        this();
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
                '}';
    }
}
