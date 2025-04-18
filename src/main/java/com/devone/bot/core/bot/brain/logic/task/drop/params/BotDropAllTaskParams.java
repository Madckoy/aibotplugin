package com.devone.bot.core.bot.brain.logic.task.drop.params;

import com.devone.bot.core.bot.brain.logic.task.params.BotLocationParams;

public class BotDropAllTaskParams extends BotLocationParams{

    public BotDropAllTaskParams() {
        super();
        setIcon("📦");
        setObjective("Drop All items");
    }
}
