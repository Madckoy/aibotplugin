package com.devone.bot.core.logic.task.hand.attack.params;

import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotHandAttackTaskParams extends BotHandTaskParams {
    public double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    public BotBlockData target = new BotBlockData();

    public BotHandAttackTaskParams() {
        super(); // Важно вызвать родительский конструктор
        setIcon("⚔️");
        setObjective("Attack");

        // Загрузка параметров из родительского класса
        BotHandAttackTaskParams loaded = loadOrCreate(BotHandAttackTaskParams.class);

        this.damage = loaded.damage;
        this.target = loaded.target;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    // Конструктор с параметрами (animal и damage)
    public BotHandAttackTaskParams(BotBlockData target, double damage) {
        super(); // Важно вызвать родительский конструктор
        this.target = target;
        this.damage = damage;
        setIcon("⚔️");
        setObjective("Attack");
        
        // Загрузка параметров из родительского класса
        BotHandAttackTaskParams loaded = loadOrCreate(BotHandAttackTaskParams.class);

        this.damage = loaded.damage;  // Если есть параметры в файле — они перезапишут дефолтные
        this.target = loaded.target;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
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
    public String toString() {
        return "BotHandAttackTaskParams{" +
                "damage=" + damage +
                '}';
    }
}
