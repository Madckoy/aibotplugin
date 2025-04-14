package com.devone.bot.core.logic.task.decision.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;

public class BotDecisionMakeTaskParams extends BotTaskParams {

    private String icon = "🎲";
    private String objective = "Roll a dice";

    public BotDecisionMakeTaskParams() {
        super(BotDecisionMakeTaskParams.class.getSimpleName());
        setDefaults();
    }

    @Override
    public Object setDefaults(){
        config.set("decision.make.icon", this.icon);
        config.set("decision.make.objective", this.objective);
        super.setDefaults();
        return this;
    }

}
