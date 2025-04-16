package com.devone.bot.core.logic.task.hand.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandTaskParams extends BotTaskParams {

    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;

    private transient BotBlockData target = new BotBlockData();

    public BotHandTaskParams() {
        setIcon("✋🏻");
        setObjective("Hand");

        BotHandTaskParams loaded = loadOrCreate(BotHandTaskParams.class);
        
        this.damage = loaded.damage;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public BotHandTaskParams(BotBlockData target) {
        this(); // загружаем из файла
        this.target = target;
    }

    public BotHandTaskParams(BotBlockData target, double damage) {
        this(); // загружаем из файла
        this.target = target;
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public BotBlockData getTarget() {
        return target;
    }

    public void setTarget(BotBlockData target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "BotHandTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
