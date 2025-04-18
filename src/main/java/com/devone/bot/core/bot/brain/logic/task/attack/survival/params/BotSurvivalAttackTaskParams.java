package com.devone.bot.core.bot.brain.logic.task.attack.survival.params;

import com.devone.bot.core.bot.brain.logic.task.params.BotTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.BotConstants;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;

public class BotSurvivalAttackTaskParams extends BotTaskParams {
    private BotBlockData target;
    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;

    // Константы для иконки и цели
    private static final String DEFAULT_ICON = "જ⁀➴";
    private static final String DEFAULT_OBJECTIVE = "Survival strike (Teleport & Strike)";
    
    public BotSurvivalAttackTaskParams() {
        super();
        setIcon(DEFAULT_ICON);
        setObjective(DEFAULT_OBJECTIVE);
        this.target = null;
    }

    public BotSurvivalAttackTaskParams(BotBlockData target, double damage) {
        super();
        this.target = target;
        this.damage = damage;
        setIcon(DEFAULT_ICON);
        setObjective(DEFAULT_OBJECTIVE);
    }

    public static BotSurvivalAttackTaskParams clone(BotSurvivalAttackTaskParams source) {
        BotSurvivalAttackTaskParams target = new BotSurvivalAttackTaskParams();
        target.setIcon(source.getIcon());
        target.setObjective(source.getObjective());
        target.setDamage(source.getDamage());
        target.setTarget(source.getTarget());
        return target;
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
    public String toString() {
        return "BotSurvivalAttackTaskParams{" +
                "damage=" + damage +
                ", target=" + target +
                '}';
    }
}
