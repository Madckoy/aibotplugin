package com.devone.bot.core.logic.task.idle.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;

public class BotIdleTaskParams extends BotTaskParams {

    private long timeout = BotConstants.DEFAULT_TASK_TIMEOUT;
    private String icon = "🍹";
    private String objective = "Idle";

    public BotIdleTaskParams() {
        super(BotIdleTaskParams.class.getSimpleName());
        setDefaults();
    }

    public BotIdleTaskParams setDefaults() {
        config.set("idle.icon", this.icon);
        config.set("idle.objective", this.objective);
        config.set("idle.timeout", this.timeout);
        super.setDefaults();
        return this;
    }
}
