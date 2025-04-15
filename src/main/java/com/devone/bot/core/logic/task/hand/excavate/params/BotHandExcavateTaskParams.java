package com.devone.bot.core.logic.task.hand.excavate.params;

import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandExcavateTaskParams extends BotHandTaskParams {
    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    private BotBlockData target = new BotBlockData();
    private String icon = "⛏";
    private String objective = "Excavate";

    public BotHandExcavateTaskParams(BotBlockData target, double damage) {
        super(BotHandExcavateTaskParams.class.getSimpleName());
        this.target = target;
        this.damage = damage;
        setIcon(icon);
        setObjective(objective);        
    }
    
    public BotHandExcavateTaskParams(BotBlockData target) {
        super(BotHandExcavateTaskParams.class.getSimpleName());
        this.target = target;
        setIcon(icon);
        setObjective(objective);
    }

    public BotHandExcavateTaskParams(double damage) {
        super(BotHandExcavateTaskParams.class.getSimpleName());
        this.damage = damage;
        setIcon(icon);
        setObjective(objective);
    }
    public BotHandExcavateTaskParams() {
        super(BotHandExcavateTaskParams.class.getSimpleName());
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
        config.set("hand.excavate.damage", this.damage);
        super.setDefaults();
        return this;
    }

    @Override
    public String toString() {
        return "BotUseHandTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
