package com.devone.bot.core.logic.task.playerlinked.protect.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;

public class BotProtectTaskParams extends BotTaskParams {

    private double follow_distance = 2.5;
    private double attack_range = 10.0;

    public BotProtectTaskParams() {
        super();
        setIcon("🛡️");
        setObjective("Protect");
    }

    public double getFollowDistance() {
        return follow_distance;
    }

    public void setFollowDistance(double dis) {
        this.follow_distance = dis;
    }

    public double getAttackRange() {
        return attack_range;
    }

    public void setAttackRange(double range) {
        this.attack_range = range;
    }
}
