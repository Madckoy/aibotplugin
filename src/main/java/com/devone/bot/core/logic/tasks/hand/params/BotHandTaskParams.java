package com.devone.bot.core.logic.tasks.hand.params;

import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandTaskParams extends BotTaskParams {
    public double damage;
    public BotBlockData target;
    private boolean isLogged = true;

    public BotHandTaskParams(BotBlockData target, double damage, boolean isLogged) {
        this.target = target;
        this.damage = damage;
        this.isLogged = isLogged;
    }
    
    public BotHandTaskParams(BotBlockData target, double damage) {
        this.target = target;
        this.damage = damage;
    }
    public BotHandTaskParams(BotBlockData target) {
        this.target = target;
        this.damage = 5.0;
    }

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
    public BotBlockData getTarget() {
        return target;
    }
    public void setTarget(BotBlockData target) {
        this.target = target;
    }
    public boolean isLogged() {
        return isLogged;
    }
    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }
    @Override
    public String toString() {
        return "BotUseHandTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
