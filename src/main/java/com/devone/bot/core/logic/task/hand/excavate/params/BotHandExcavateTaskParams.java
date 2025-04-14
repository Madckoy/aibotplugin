package com.devone.bot.core.logic.task.hand.excavate.params;

import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;

import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandExcavateTaskParams extends BotHandTaskParams {
    public double damage;
    public BotBlockData target;
    private boolean isLogged = true;

    public BotHandExcavateTaskParams(BotBlockData target, double damage, boolean isLogged) {
        this.target = target;
        this.damage = damage;
        this.isLogged = isLogged;
    }
    
    public BotHandExcavateTaskParams(BotBlockData target, double damage) {
        this.target = target;
        this.damage = damage;
    }
    public BotHandExcavateTaskParams(BotBlockData target) {
        this.target = target;
        this.damage = 5.0;
    }

    public BotHandExcavateTaskParams(double damage) {
        this.damage = damage;
    }
    public BotHandExcavateTaskParams() {
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
