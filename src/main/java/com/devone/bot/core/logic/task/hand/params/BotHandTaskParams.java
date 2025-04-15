package com.devone.bot.core.logic.task.hand.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandTaskParams extends BotTaskParams {
    public double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    public BotBlockData target;
    private String icon = "✋🏻";
    private String objective = "Hand";

    public BotHandTaskParams(BotBlockData target, double damage) {
        super(BotHandTaskParams.class.getSimpleName());
        this.target = target;
        this.damage = damage;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }
    
    public BotHandTaskParams(BotBlockData target) {
        super(BotHandTaskParams.class.getSimpleName());
        this.target = target;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public BotHandTaskParams(double damage) {
        super(BotHandTaskParams.class.getSimpleName());
        this.damage = damage;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public BotHandTaskParams() {
        super(BotHandTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }    
    
    public BotHandTaskParams(String class_name) {
        super(class_name);
        setIcon(icon);
        setObjective(objective);
        setDefaults();
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

    public Object setDefaults() {
        config.set("hand.damage", this.damage);
        super.setDefaults();
        return this;
    }

    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        damage = ((BotHandTaskParams)source).getDamage();
        return this;
    }

    @Override
    public String toString() {
        return "BotHandUseTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
