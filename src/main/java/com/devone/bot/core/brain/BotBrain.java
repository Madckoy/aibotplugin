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

    private transient boolean reactionInProgress = false;
    private transient String currentReactionOwner = null;

    private transient int thinkingTicks = 0;
    private transient long lastThinkingTimestamp = 0;
    private transient boolean autoPickUpItems = true;

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

    // 🧠 Автоматический сбор предметов
    public void setAutoPickupItems(boolean pickup) {
        this.autoPickUpItems = pickup;
    }

    public boolean getAutoPickupItems() {
        return autoPickUpItems;
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

    // 🧠 Реактивность
    public boolean isReactionInProgress() {
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), owner.isLogged(),
                owner.getId() + " ⚛️ Get Reactive reaction in progress status: " + this.reactionInProgress);
        return reactionInProgress;
    }

    public void setReactionInProgress(boolean value) {
        this.reactionInProgress = value;
        BotLogger.debug(BotUtils.getActiveTaskIcon(owner), owner.isLogged(),
                owner.getId() + " ⚛️ Set Reactive reaction in progress status: " + this.reactionInProgress);
    }

    public String getCurrentReactionOwner() {
        return currentReactionOwner;
    }

    public void setCurrentReactionOwner(String uuid) {
        this.currentReactionOwner = uuid;
        BotLogger.debug("🧠", owner.isLogged(),
                owner.getId() + " ⚛️ Установлен владелец реактивной задачи: " + uuid);
    }

    public void clearCurrentReactionOwner() {
        this.currentReactionOwner = null;
        BotLogger.debug("🧠", owner.isLogged(),
                owner.getId() + " ⚛️ Владелец реактивной задачи очищен");
                
    }

    // 🧠 Проверка: текущая задача — владелец реакции?
    public boolean isReactionOwnedBy(BotTask<?> task) {
        if (!reactionInProgress || currentReactionOwner == null || task == null)
            return false;

        boolean result = currentReactionOwner.equals(task.getUUID());

        BotLogger.debug("🧠", owner.isLogged(), owner.getId() + " 🧩 Проверка владения реакцией: " + result +
                " (expected=" + currentReactionOwner + ", actual=" + task.getUUID() + ")");

        return result;
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
