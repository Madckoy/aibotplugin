package com.devone.bot.core.task.active.fishing.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;

public class BotFishingTaskParams extends BotTaskParams {

    public BotFishingTaskParams() {
        super();
        setIcon("🎣");
        setObjective("Fishing while in water");
    }

    @Override
    public String toString() {
        return "BotFishingTaskParams{" +
                "icon=" + getIcon() +
                ", objective=" + getObjective() +
                '}';
    }
}
