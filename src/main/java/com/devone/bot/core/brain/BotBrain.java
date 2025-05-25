package com.devone.bot.core.brain;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.perseption.BotYawChangeListener;
import com.devone.bot.core.brain.perseption.BotYawBasedSceneRefresher;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotBrain {

    private transient Bot owner;

    private transient int thinkingTicks = 0;
    private transient long lastThinkingTimestamp = 0;

    private transient BotMemoryV2 memoryV2 = null;
    private transient BotSceneData sceneData = null;

    private long memoryExpirationMillis = BotConstants.DEFAULT_MEMORY_EXPIRATION;

    private BotYawChangeListener yawListener;

    public BotBrain(Bot bot) {
        this.owner = bot;
        this.memoryV2 = new BotMemoryV2(this);
        this.sceneData = null;
        setYawListener(new BotYawBasedSceneRefresher());
    }

    // 🧠 Получение текущей задачи
    public BotTask<?> getCurrentTask() throws Exception{
        return owner.getActiveTask();
    }

    public void setMemoryV2(BotMemoryV2 memoryV2) {
        this.memoryV2 = memoryV2;
    }

    public BotMemoryV2 getMemoryV2() {
        return memoryV2;
    }

    
    public void setSceneData(BotSceneData sceneData) {
        this.sceneData = sceneData;
    }

    public BotSceneData getSceneData() {
        return this.sceneData;
    }


    // 🧠 Мышление
    public void markThinkingCycle() {
        thinkingTicks++;
        lastThinkingTimestamp = System.currentTimeMillis();
    }

    public void resetThinkingCycle() {
        thinkingTicks = 0;
    }

    public int getThinkingTicks() {
        return thinkingTicks;
    }

    public long getLastThinkingTimestamp() {
        return lastThinkingTimestamp;
    }

    // 🧠 Память: срок хранения
    public long getMemoryExpirationMillis() {
        return memoryExpirationMillis;
    }

    public void setMemoryExpirationMillis(long memoryExpirationMillis) {
        this.memoryExpirationMillis = memoryExpirationMillis;
    }

    public void setYawListener(BotYawChangeListener listener) {
        this.yawListener = listener;
    }
    
    public void notifyYawChanged(float newYaw) {
        if (yawListener != null) {
            yawListener.onYawChanged(owner, newYaw);
        }
    }

}
