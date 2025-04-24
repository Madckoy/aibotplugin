package com.devone.bot.core.brain;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemory;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotBrain {

    private transient Bot owner;

    private transient boolean reactionInProgress = false;
    private transient String currentReactionOwner = null;

    private int thinkingTicks = 0;
    private long lastThinkingTimestamp = 0;
    private boolean autoPickUpItems = true;
    private transient BotMemory memory = null;

    private long memoryExpirationMillis = BotConstants.DEFAULT_MEMORY_EXPIRATION;

    public BotBrain(Bot bot) {
        this.owner = bot;
        this.memory = new BotMemory(this);
    }

    // 🧠 Получение текущей задачи
    public BotTask<?> getCurrentTask() {
        return owner.getActiveTask();
    }

    // 🧠 Автоматический сбор предметов
    public void setAutoPickupItems(boolean pickup) {
        this.autoPickUpItems = pickup;
    }

    public boolean getAutoPickupItems() {
        return autoPickUpItems;
    }

    // 🧠 Память
    public BotMemory getMemory() {
        return memory;
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
        BotLogger.debug(owner.getActiveTask().getIcon(), true,
                owner.getId() + " ⚛️ Get Reactive reaction in progress status: " + this.reactionInProgress);
        return reactionInProgress;
    }

    public void setReactionInProgress(boolean value) {
        this.reactionInProgress = value;
        BotLogger.debug(owner.getActiveTask().getIcon(), true,
                owner.getId() + " ⚛️ Set Reactive reaction in progress status: " + this.reactionInProgress);
    }

    public String getCurrentReactionOwner() {
        return currentReactionOwner;
    }

    public void setCurrentReactionOwner(String uuid) {
        this.currentReactionOwner = uuid;
        BotLogger.debug("🧠", true,
                owner.getId() + " ⚛️ Установлен владелец реактивной задачи: " + uuid);
    }

    public void clearCurrentReactionOwner() {
        this.currentReactionOwner = null;
        BotLogger.debug("🧠", true,
                owner.getId() + " ⚛️ Владелец реактивной задачи очищен");
    }

    // 🧠 Проверка: текущая задача — владелец реакции?
    public boolean isReactionOwnedBy(BotTask<?> task) {
        if (!reactionInProgress || currentReactionOwner == null || task == null) return false;

        boolean result = currentReactionOwner.equals(task.getUUID());

        BotLogger.debug("🧠", true, owner.getId() + " 🧩 Проверка владения реакцией: " + result +
                " (expected=" + currentReactionOwner + ", actual=" + task.getUUID() + ")");

        return result;
    }
}
