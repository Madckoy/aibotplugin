package com.devone.bot.core.logic.task.drop.params;

import com.devone.bot.core.logic.task.params.BotLocationParams;

public class BotDropAllTaskParams extends BotLocationParams{

    public BotDropAllTaskParams() {
        super();
        setIcon("📦");
        setObjective("Drop All items");
    }
}
