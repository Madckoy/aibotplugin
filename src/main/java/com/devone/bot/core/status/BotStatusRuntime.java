package com.devone.bot.core.status;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;

public class BotStatusRuntime {
    private Bot owner;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotCoordinate3D currentLocation;

    private BotCoordinate3D targetLocation;  // Новое свойство для целевой локации

    // Другие состояния
    private boolean stuck;
    private int stuckCount;
    private long killedMobs;
    private long brokebBlocks;
    private long teleportUsed;

    public BotStatusRuntime(Bot bot) {
        this.owner = bot;
        this.currentLocation = getCurrentLocation();  // Инициализируем с текущей локацией
        this.targetLocation = null;  // Начальное значение для targetLocation
        this.stuck = false;
        this.stuckCount = 0;
        this.killedMobs = 0;
        this.brokebBlocks = 0;
        this.teleportUsed = 0;
    }

    public BotTask getCurrentTask() {
        return owner.getLifeCycle().getTaskStackManager().getActiveTask();
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public BotCoordinate3D getCurrentLocation() {
        if (owner.getNPC() != null) {
            // Получаем текущую локацию NPC
            BotCoordinate3D newLocation = BotCoordinate3DHelper.convertFrom(owner.getNPC().getStoredLocation());
            currentLocation = newLocation;
        } else {
            // Если NPC не найден, возвращаем null
            return null;
        }
        return currentLocation;
    }

    public void setCurrentLocation(BotCoordinate3D location) {
        this.currentLocation = location;
    }

    // Флаг застревания
    public boolean isStuck() {
        return stuck;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
        incrementStuckCount();
    }

    // Счётчик застревания
    public int getStuckCount() {
        return stuckCount;
    }

    public void incrementStuckCount() {
        this.stuckCount++;
    }

    public void resetStuckCount() {
        this.stuckCount = 0;
    }

    // Геттер и Сеттер для targetLocation
    public BotCoordinate3D getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(BotCoordinate3D targetLocation) {
        this.targetLocation = targetLocation;
    }

    public void killedMobsIncrease() {
        this.killedMobs = killedMobs + 1;
    }

    public void brokenBlocksIncrease() {
        this.brokebBlocks = brokebBlocks + 1;
    }

    public long getMobsKilled() {
        return killedMobs;
    }

    public long getBrokenBlocks() {
        return brokebBlocks;
    }
    public void resetBrokenBlocks() {
        this.brokebBlocks = 0;
    }
    public void resetKilledMobs() {
        this.killedMobs = 0;
    }

    public void resetMobsKilled() {
        this.killedMobs = 0;
    }

    public void teleportUsedIncrease() {
        this.teleportUsed = teleportUsed + 1;
    }

    public long getTeleportUsed() {
        return teleportUsed;
    }
    public void resetTeleportUsed() {
        this.teleportUsed = 0;
    }
}
