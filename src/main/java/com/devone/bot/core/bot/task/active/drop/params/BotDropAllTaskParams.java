package com.devone.bot.core.bot.task.active.drop.params;

import com.devone.bot.core.bot.task.passive.params.BotLocationParams;

public class BotDropAllTaskParams extends BotLocationParams{

    public BotDropAllTaskParams() {
        super();
        setIcon("📦");
        setObjective("Drop All items");
    }
}
