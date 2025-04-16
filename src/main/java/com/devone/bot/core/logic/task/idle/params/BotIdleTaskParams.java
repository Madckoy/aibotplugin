package com.devone.bot.core.logic.task.idle.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;

public class BotIdleTaskParams extends BotTaskParams {

    private long timeout = BotConstants.DEFAULT_TASK_TIMEOUT;

    public BotIdleTaskParams() {
        setIcon("🍹");
        setObjective("Idle");

        BotIdleTaskParams loaded = loadOrCreate(BotIdleTaskParams.class);
        
        this.timeout = loaded.timeout;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
