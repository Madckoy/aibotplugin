package com.devone.bot.core.brain;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.brain.memory.BotMemory;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.blocks.BotLocationHelper;


public class BotBrain {

    private transient Bot owner;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotLocation currentLocation;

    private BotLocation targetLocation;  // Новое свойство для целевой локации
    // auto pick ip items
    private boolean autoPickUpItems = true;

    private BotMemory memory = new BotMemory();

    private int thinkingTicks = 0;
    private long lastThinkingTimestamp = 0;


    public BotBrain(Bot bot) {
        this.owner = bot;
        this.currentLocation = getCurrentLocation();  // Инициализируем с текущей локацией
        this.targetLocation = null;  // Начальное значение для targetLocation
    }

    public BotTask<?> getCurrentTask() {
        BotTask<?> task = owner.getLifeCycle().getTaskStackManager().getActiveTask();
        if (task != null) {
            return task; // Нам не нужно дополнительное приведение типов
        }
        return null; // или выбросить исключение, если задача не найдена
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public BotLocation getCurrentLocation() {
        if (owner.getNPC() != null) {
            // Получаем текущую локацию NPC
            BotLocation newLocation = BotLocationHelper.convertFrom(owner.getNPC().getStoredLocation());
            currentLocation = newLocation;
        } else {
            // Если NPC не найден, возвращаем null
            return null;
        }
        return currentLocation;
    }

    public void setAutoPickupItems(boolean pickup) {
        autoPickUpItems = pickup;
    }

    public boolean getAutoPickupItems() {
        return autoPickUpItems;
    }

    public void setCurrentLocation(BotLocation location) {
        this.currentLocation = location;
    }


    // Геттер и Сеттер для targetLocation
    public BotLocation getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(BotLocation targetLocation) {
        this.targetLocation = targetLocation;
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
    
}
