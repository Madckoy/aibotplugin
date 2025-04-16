package com.devone.bot.core.logic.task.decision.params;

import com.devone.bot.core.logic.task.attack.survival.params.BotSurvivalAttackTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;

public class BotDecisionMakeTaskParams extends BotTaskParams {

    public BotDecisionMakeTaskParams() {
        setIcon("🎲");
        setObjective("Roll a dice");
        // Загрузка параметров из родительского класса
        BotSurvivalAttackTaskParams loaded = loadOrCreate(BotSurvivalAttackTaskParams.class);
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }
}
