package com.devone.bot.core.bot.behaviour.task.idle.params;

import com.devone.bot.core.bot.behaviour.task.params.BotTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.BotConstants;

public class BotIdleTaskParams extends BotTaskParams {

    private long timeout = BotConstants.DEFAULT_TASK_TIMEOUT;

    public BotIdleTaskParams() {
        super();

        setIcon("🍹");
        setObjective("Idle");
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
