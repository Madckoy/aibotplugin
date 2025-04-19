package com.devone.bot.core.bot.task.active.hand.excavate.params;

import com.devone.bot.core.bot.task.active.hand.params.BotHandTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotHandExcavateTaskParams extends BotHandTaskParams {

    public BotHandExcavateTaskParams() {
        super();
        setIcon("⛏");
        setObjective("Hit");
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
