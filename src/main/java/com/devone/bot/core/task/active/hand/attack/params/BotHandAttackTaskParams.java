package com.devone.bot.core.task.active.hand.attack.params;

import com.devone.bot.core.task.active.hand.params.BotHandTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotHandAttackTaskParams extends BotHandTaskParams {

    public BotHandAttackTaskParams() {
        super(); // Важно вызвать родительский конструктор
        setIcon("⚔️");
        setObjective("Attack");
    }

    // Конструктор с параметрами (animal и damage)
    public BotHandAttackTaskParams(BotBlockData target, double damage) {
        super(); // Важно вызвать родительский конструктор
        setDamage(damage);
        setIcon("⚔️");
        setObjective("Attack");
    }

    @Override
    public String toString() {
        return "BotHandAttackTaskParams{" +
                "damage=" + getDamage() +
                '}';
    }
}
