package com.devone.bot.core.status;

import java.util.ArrayList;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.scene.BotSceneData;

public class BotStatusRuntime {
    private Bot owner;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotCoordinate3D currentLocation;

    private BotCoordinate3D targetLocation;  // Новое свойство для целевой локации
    // auto pick ip items
    private boolean autoPickUpItems = true;
    // Другие состояния
    private boolean stuck;
    private int stuckCount;
    private ArrayList<String> killedMobs;
    private ArrayList<String> brokenBlocks;
    private long teleportUsed;
    protected BotSceneData sceneData;

    public BotStatusRuntime(Bot bot) {
        this.owner = bot;
        this.currentLocation = getCurrentLocation();  // Инициализируем с текущей локацией
        this.targetLocation = null;  // Начальное значение для targetLocation
        this.stuck = false;
        this.stuckCount = 0;
        this.killedMobs   = new ArrayList<String>();
        this.brokenBlocks = new ArrayList<String>();
        this.teleportUsed = 0;
        this.sceneData = null;
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

    public void setAutoPickupItems(boolean pickup) {
        autoPickUpItems = pickup;
    }

    public boolean getAutoPickupItems() {
        return autoPickUpItems;
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
        BotLogger.info("🔔", true, "BotStatusRuntime: set Stuck="+stuck);
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

    public void killedMobsIncrease(String mobName) {
        this.killedMobs.add(mobName);
    }

    public void brokenBlocksIncrease(String blockName) {
        this.brokenBlocks.add(blockName);
    }

    public ArrayList<String> getMobsKilled() {
        return killedMobs;
    }

    public ArrayList<String> getBlocksBroken() {
        return brokenBlocks;
    }

    public void teleportUsedIncrease() {
        this.teleportUsed = teleportUsed + 1;
    }

    public long getTeleportUsed() {
        return teleportUsed;
    }

    public void setSceneData(BotSceneData sceneData) {
        this.sceneData = sceneData;
    }

    public BotSceneData getSceneData() {
        return this.sceneData;
    }
}
