package com.devone.bot.core.logic.task.drop.params;

import com.devone.bot.core.logic.task.params.BotCoordinate3DParams;

public class BotDropAllTaskParams extends BotCoordinate3DParams{

    private String icon = "📦";
    private String objective = "Drop All items";

    public BotDropAllTaskParams() {
        super(BotDropAllTaskParams.class.getSimpleName());
        setDefaults();
    }

    @Override
    public Object setDefaults(){
        config.set("drop.all.icon", this.icon);
        config.set("drop.all.objective", this.objective);
        super.setDefaults();
        return this;
    }

}
