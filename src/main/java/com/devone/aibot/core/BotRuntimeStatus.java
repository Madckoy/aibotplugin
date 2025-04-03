package com.devone.aibot.core;

import org.bukkit.Location;

public class BotRuntimeStatus {
    private Bot owner;

    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private Location currentLocation;
    private Location lastKnownLocation;
    private Location targetLocation;  // Новое свойство для целевой локации

    // Другие состояния
    private boolean isStuck;
    private int stuckCount;
    private int mobsKilled;

    public BotRuntimeStatus(Bot bot) {
        this.owner = bot;
        this.currentLocation = getCurrentLocation();  // Инициализируем с текущей локацией
        this.lastKnownLocation = currentLocation;  // Начальное значение lastKnownLocation
        this.targetLocation = null;  // Начальное значение для targetLocation
        this.isStuck = false;
        this.stuckCount = 0;
        this.mobsKilled = 0;
    }

    // Получение и обновление currentLocation с проверкой на застревание
    public Location getCurrentLocation() {
        if (owner.getNPC() != null) {
            // Получаем текущую локацию NPC
            Location newLocation = owner.getNPC().getStoredLocation();

            // Проверка на застревание: если NPC не двигается и его локация не изменилась
            if (newLocation.equals(currentLocation)) {
                // Устанавливаем флаг застревания, если локация не изменилась
                this.isStuck = true;

                // Не обновляем локацию, если бот не двигается
                return currentLocation;
            } else {
                // Если локация изменилась, обновляем currentLocation и lastKnownLocation
                currentLocation = newLocation;
                lastKnownLocation = newLocation;
                this.isStuck = false;  // Сбрасываем флаг застревания, так как бот двигается
            }
        } else {
            // Если NPC не найден, возвращаем lastKnownLocation
            return lastKnownLocation;
        }
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
        this.lastKnownLocation = location;  // Обновляем lastKnownLocation, когда перемещаемся
    }

    // Получение lastKnownLocation, если не можем получить актуальную currentLocation
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location location) {
        this.lastKnownLocation = location;
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
    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
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
