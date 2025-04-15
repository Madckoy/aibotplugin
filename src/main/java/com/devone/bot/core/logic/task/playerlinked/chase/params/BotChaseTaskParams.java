package com.devone.bot.core.logic.task.playerlinked.chase.params;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;

public class BotChaseTaskParams extends BotTaskParams {

    private double chase_distance = 2.5;
    private double attack_range = 10.0;
    private String icon = "🎯";
    private String objective = "Chase";


    public BotChaseTaskParams() {
        super(BotChaseTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    @Override
    public Object setDefaults() {
        config.set("chase.distance", chase_distance); // Расстояние следования за игроком
        config.set("chase.attack.range", attack_range); // Дистанция атаки на мобов
        super.setDefaults();
        return this;
    }

    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        chase_distance = ((BotChaseTaskParams)source).getChaseDistance();
        attack_range = ((BotChaseTaskParams)source).getAttackRange();
        return this;
    }

    public double getChaseDistance() {
        return chase_distance;
    }

    public double getAttackRange() {
        return attack_range;
    }
}
