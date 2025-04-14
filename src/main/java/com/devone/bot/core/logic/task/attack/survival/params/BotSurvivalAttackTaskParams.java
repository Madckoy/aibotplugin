package com.devone.bot.core.logic.task.attack.survival.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotSurvivalAttackTaskParams extends BotTaskParams {
    private BotBlockData target;
    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    private String icon = "જ⁀➴";
    private String objective = "Survival strike (Teleport & Strike)";
    
    public BotSurvivalAttackTaskParams() {
        super(BotSurvivalAttackTaskParams.class.getSimpleName());
        this.target = null;
        setDefaults();
    }

    public BotSurvivalAttackTaskParams(BotBlockData target, double damage) {
        super(BotSurvivalAttackTaskParams.class.getSimpleName());

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

    @Override
    public Object setDefaults() {
        config.set("survival.strike.icon", this.icon);
        config.set("survival.strike.objective", this.objective);
        config.set("survival.strike.damage", this.damage);
        super.setDefaults();
        return this;
    }
    @Override
    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        icon = ((BotSurvivalAttackTaskParams)source).getIcon();
        objective = ((BotSurvivalAttackTaskParams)source).getObjective();
        damage = ((BotSurvivalAttackTaskParams)source).getDamage();
        return this;
    }
}