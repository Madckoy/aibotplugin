package com.devone.bot.core.bot.task.active.hand.attack.params;

import com.devone.bot.core.bot.task.active.hand.params.BotHandTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotHandAttackTaskParams extends BotHandTaskParams {

    public BotBlockData target = new BotBlockData();

    public BotHandAttackTaskParams() {
        super(); // Важно вызвать родительский конструктор
        setIcon("⚔️");
        setObjective("Attack");
    }

    // Конструктор с параметрами (animal и damage)
    public BotHandAttackTaskParams(BotBlockData target, double damage) {
        super(); // Важно вызвать родительский конструктор
        this.target = target;
        setDamage(damage);
        setIcon("⚔️");
        setObjective("Attack");
    }

    public BotBlockData getTarget() {
        return target;
    }

    public void setTarget(BotBlockData target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "BotHandAttackTaskParams{" +
                "damage=" + getDamage() +
                '}';
    }
}
