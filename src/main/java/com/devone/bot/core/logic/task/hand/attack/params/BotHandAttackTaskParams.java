package com.devone.bot.core.logic.task.hand.attack.params;
import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandAttackTaskParams extends BotHandTaskParams {
    public double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    public BotBlockData target;
  
    private String icon = "⚔️";
    private String objective = "Attack";

    public BotHandAttackTaskParams(BotBlockData target, double dmg) {
        super(BotHandAttackTaskParams.class.getSimpleName());
        this.target = target;
        this.damage = dmg;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }
    
    public BotHandAttackTaskParams(BotBlockData target) {
        super(BotHandAttackTaskParams.class.getSimpleName());
        this.target = target;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public BotHandAttackTaskParams(double damage) {
        super(BotHandAttackTaskParams.class.getSimpleName());
        this.damage = damage;
        setIcon(icon);
        setObjective(objective);
        setDefaults();   
    }

    public BotHandAttackTaskParams() {
        super(BotHandAttackTaskParams.class.getSimpleName());
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

       @Override
    public Object setDefaults(){
        config.set("hand.attack.damage", this.damage);
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
