package com.devone.bot.core.bot.task.active.idle.params;

import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotIdleTaskParams extends BotTaskParams {

    private long timeout = BotConstants.DEFAULT_IDLE_TIMEOUT;

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
