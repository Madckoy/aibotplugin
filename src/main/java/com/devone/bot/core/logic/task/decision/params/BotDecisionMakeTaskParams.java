package com.devone.bot.core.logic.task.decision.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;

public class BotDecisionMakeTaskParams extends BotTaskParams {

    public BotDecisionMakeTaskParams() {
        super();
        setIcon("🎲");
        setObjective("Roll a dice");
    }

    public static BotDecisionMakeTaskParams clone(BotDecisionMakeTaskParams source) {
        BotDecisionMakeTaskParams target = new BotDecisionMakeTaskParams();
        return target;
    }
}
