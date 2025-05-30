package com.devone.bot.core.task.active.hand.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotHandTaskParams extends BotTaskParams {

    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;

    public BotHandTaskParams() {
        setIcon("✋🏻");
        setObjective("Hand");
    }

    public BotHandTaskParams(BotBlockData target) {
        this(); // загружаем из файла
    }

    public BotHandTaskParams(BotBlockData target, double damage) {
        this(); // загружаем из файла
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public String toString() {
        return "BotHandTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
