package com.devone.bot.core.logic.task.playerlinked.protect.params;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;

public class BotProtectTaskParams extends BotTaskParams {

    private double follow_distance = 2.5;
    private double attack_range = 10.0;
    private String icon = "🛡️";
    private String objective = "Protect";


    public BotProtectTaskParams() {
        super(BotProtectTaskParams.class.getSimpleName());
        setDefaults();
    }

    @Override
    public Object setDefaults() {
        config.set("protect.distance", follow_distance); // Расстояние следования за игроком
        config.set("protect.attack.range", attack_range); // Дистанция атаки на мобов
        config.set("protect.icon", icon);
        config.set("protect.objective", objective);
        super.setDefaults();
        return this;
    }

    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        follow_distance = ((BotProtectTaskParams)source).getFollowDistance();
        attack_range = ((BotProtectTaskParams)source).getAttackRange();
        icon = ((BotProtectTaskParams)source).getIcon();
        objective = ((BotProtectTaskParams)source).getObjective();
        return this;
    }

    public double getFollowDistance() {
        return follow_distance;
    }

    public double getAttackRange() {
        return attack_range;
    }
}
