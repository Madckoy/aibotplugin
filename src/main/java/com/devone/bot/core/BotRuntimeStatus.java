package com.devone.bot.core;

import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotCoordinate3DHelper;

public class BotRuntimeStatus {
    private Bot owner;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotCoordinate3D currentLocation;

    private BotCoordinate3D targetLocation;  // Новое свойство для целевой локации

    // Другие состояния
    private boolean isStuck;
    private int stuckCount;
    private int mobsKilled;

    public BotRuntimeStatus(Bot bot) {
        this.owner = bot;
        this.currentLocation = getCurrentLocation();  // Инициализируем с текущей локацией
        this.targetLocation = null;  // Начальное значение для targetLocation
        this.isStuck = false;
        this.stuckCount = 0;
        this.mobsKilled = 0;
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public BotCoordinate3D getCurrentLocation() {
        if (owner.getNPC() != null) {
            // Получаем текущую локацию NPC
            BotCoordinate3D newLocation = BotCoordinate3DHelper.convertFrom(owner.getNPC().getStoredLocation());

            // Проверка на застревание: если NPC не двигается и его локация не изменилась
            if (newLocation.equals(currentLocation)) {
                // Устанавливаем флаг застревания, если локация не изменилась
                this.isStuck = true;

                // Не обновляем локацию, если бот не двигается
                return currentLocation;
            } else {
                // Если локация изменилась, обновляем currentLocation и lastKnownLocation
                currentLocation = newLocation;
                this.isStuck = false;  // Сбрасываем флаг застревания, так как бот двигается
            }
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
        return isStuck;
    }

    public void setStuck(boolean stuck) {
        this.isStuck = stuck;
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

    public void mobKilledAdd(int count) {
        this.mobsKilled = mobsKilled + count;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void resetMobsKilled() {
        this.mobsKilled = 0;
    }

}
