package com.devone.bot.core.logic.tasks.hand.params;

import com.devone.bot.core.logic.tasks.params.BotTaskParams;

public class BotHandTaskParams extends BotTaskParams {
    public double damage;

    public BotHandTaskParams(double damage) {
        this.damage = damage;
    }
    public BotHandTaskParams() {
        this.damage = 5.0;
    }
    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
    @Override
    public String toString() {
        return "BotUseHandTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
