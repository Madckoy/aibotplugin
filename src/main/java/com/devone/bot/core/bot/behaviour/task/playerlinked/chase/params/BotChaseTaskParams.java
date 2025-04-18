package com.devone.bot.core.bot.behaviour.task.playerlinked.chase.params;

import com.devone.bot.core.bot.behaviour.task.params.BotTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;

public class BotChaseTaskParams extends BotTaskParams {

    private double chaseDistance = 2.5;
    private double attackRange = 10.0;

    // 🧠 Цель в рантайме, не сохраняется
    private transient BotBlockData target;

    public BotChaseTaskParams() {
        super();
        setIcon("🎯");
        setObjective("Chase");
    }

    public BotChaseTaskParams(BotBlockData tgt) {
        this(); // загружаем всё из файла
        this.target = tgt; // но переопределяем runtime-цель
    }

    public double getChaseDistance() {
        return chaseDistance;
    }

    public void setChaseDistance(double distance) {
        this.chaseDistance = distance;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(double distance) {
        this.attackRange = distance;
    }

    public BotBlockData getTarget() {
        return target;
    }

    public void setTarget(BotBlockData tgt) {
        this.target = tgt;
    }
}
