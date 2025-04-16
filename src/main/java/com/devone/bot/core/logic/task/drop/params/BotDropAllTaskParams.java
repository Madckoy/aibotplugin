package com.devone.bot.core.logic.task.drop.params;

import com.devone.bot.core.logic.task.params.BotLocationParams;

public class BotDropAllTaskParams extends BotLocationParams{

    private String icon = "📦";
    private String objective = "Drop All items";

    public BotDropAllTaskParams() {
        setIcon(icon);
        setObjective(objective);
    }
}
