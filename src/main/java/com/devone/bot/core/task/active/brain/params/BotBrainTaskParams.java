package com.devone.bot.core.task.active.brain.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;

public class BotBrainTaskParams extends BotTaskParams {

    private long memoryExpirationMillis = 10 * 60 * 1000; // по умолчанию 10 минут

    public BotBrainTaskParams() {
        super();
        setIcon("🧠");
        setObjective("Think");
    }

    public static BotBrainTaskParams clone(BotBrainTaskParams source) {
        if (source == null) return new BotBrainTaskParams();
    
        BotBrainTaskParams target = new BotBrainTaskParams();
        return target;
    }

    public long getMemoryExpirationMillis() {
        return memoryExpirationMillis;
    }
    
    public void setMemoryExpirationMillis(long memoryExpirationMillis) {
        this.memoryExpirationMillis = memoryExpirationMillis;
    }

    
}
