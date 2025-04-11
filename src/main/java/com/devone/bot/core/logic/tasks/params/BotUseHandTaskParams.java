package com.devone.bot.core.logic.tasks.params;

public class BotUseHandTaskParams extends BotTaskParams {
    public double damage;

    public BotUseHandTaskParams(double damage) {
        this.damage = damage;
    }
    public BotUseHandTaskParams() {
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
