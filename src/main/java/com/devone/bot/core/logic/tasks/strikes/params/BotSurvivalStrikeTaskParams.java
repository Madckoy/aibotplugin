package com.devone.bot.core.logic.tasks.strikes.params;

import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotSurvivalStrikeTaskParams extends BotTaskParams {
    private BotBlockData target;
    private double damage;
    private boolean isLogged = true;
    
    public BotSurvivalStrikeTaskParams() {
        this.target = null;
        this.damage = 5.0;
    }

    public BotSurvivalStrikeTaskParams(BotBlockData target, double damage) {
        this.target = target;
        this.damage = damage;
    }

    public BotBlockData getTarget() {
        return target;
    }
    public void setTarget(BotBlockData target) {
        this.target = target;
    }
    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
    public boolean isLogged() {
        return isLogged;
    }
    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }
}