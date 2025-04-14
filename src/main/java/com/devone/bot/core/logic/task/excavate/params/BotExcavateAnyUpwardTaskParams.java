package com.devone.bot.core.logic.task.excavate.params;

public class BotExcavateAnyUpwardTaskParams extends BotExcavateTaskParams{

    public BotExcavateAnyUpwardTaskParams() {
        super(BotExcavateAnyUpwardTaskParams.class.getSimpleName());
        setIcon("🪨");
        setObjective("Excavate all up");
        setDefaults();
    }

}
