package com.devone.bot.core.logic.task.excavate.params;

public class BotExcavateAnyAroundParams extends BotExcavateTaskParams{

    public BotExcavateAnyAroundParams() {
        super(BotExcavateAnyAroundParams.class.getSimpleName());
        setIcon("✴");
        setObjective("Excavate all around");
        setDefaults();
    }
}
