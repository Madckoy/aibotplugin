package com.devone.bot.core.logic.task.decision.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;

public class BotDecisionMakeTaskParams extends BotTaskParams {

    public BotDecisionMakeTaskParams() {
        super(BotDecisionMakeTaskParams.class.getSimpleName());
        setIcon("🎲");
        setObjective("Roll a dice");
        setDefaults();
    }
}
