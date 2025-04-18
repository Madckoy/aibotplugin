package com.devone.bot.core.bot.behaviour.task.drop.params;

import com.devone.bot.core.bot.behaviour.task.params.BotLocationParams;

public class BotDropAllTaskParams extends BotLocationParams{

    public BotDropAllTaskParams() {
        super();
        setIcon("📦");
        setObjective("Drop All items");
    }
}
