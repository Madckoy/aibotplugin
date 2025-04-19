package com.devone.bot.core.bot.brain;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.memory.BotMemory;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;


public class BotBrain {

    private transient Bot owner;

    private transient BotMemory memory = new BotMemory();
    private transient boolean reactionInProgress = false;

    private int thinkingTicks = 0;
    private long lastThinkingTimestamp = 0;
    private boolean autoPickUpItems = true;


    public BotBrain(Bot bot) {
        this.owner = bot;
    }

    public BotTask<?> getCurrentTask() {
        BotTask<?> task = owner.getActiveTask();
        if (task != null) {
            return task; // Нам не нужно дополнительное приведение типов
        }
        return null; // или выбросить исключение, если задача не найдена
    }
    
    public void setAutoPickupItems(boolean pickup) {
        autoPickUpItems = pickup;
    }

    public boolean getAutoPickupItems() {
        return autoPickUpItems;
    }

    public BotMemory getMemory(){
        return memory;
    }

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
    
    public boolean isReactionInProgress() {
        BotLogger.debug("🧠", true, "⤴️ Get Reactive reaction in progress status:" + this.reactionInProgress);
        return reactionInProgress;
        
    }
    
    public void setReactionInProgress(boolean value) {
        this.reactionInProgress = value;
        BotLogger.debug("🧠", true, "⤵️ Set Reactive reaction in progress status:" + this.reactionInProgress);
    }

}
