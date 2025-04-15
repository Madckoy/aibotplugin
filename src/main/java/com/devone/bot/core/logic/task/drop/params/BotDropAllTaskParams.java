package com.devone.bot.core.logic.task.drop.params;

import com.devone.bot.core.logic.task.params.BotCoordinate3DParams;

public class BotDropAllTaskParams extends BotCoordinate3DParams{

    private String icon = "📦";
    private String objective = "Drop All items";

    public BotDropAllTaskParams() {
        super(BotDropAllTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }
}
