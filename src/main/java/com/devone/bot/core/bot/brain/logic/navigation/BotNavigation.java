package com.devone.bot.core.bot.brain.logic.navigation;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocationHelper;

public class BotNavigation {
    private transient Bot owner;
    // Добавляем currentLocation, lastKnownLocation и targetLocation
    private BotLocation currentLocation;
    private transient BotLocation targetLocation;  // Новое свойство для целевой локации

    public BotNavigation ()
    {   
        this.currentLocation = null; // Инициализируем с текущей локацией
        this.targetLocation = null;  // Начальное значение для targetLocation
    }

    public BotNavigation(Bot owner) {
        this();
        this.owner = owner;
        this.currentLocation = getLocation();  // Инициализируем с текущей локацией
        this.targetLocation = null;  // Начальное значение для targetLocation
    }

        // Получение и обновление currentLocation с проверкой на застревание
    public BotLocation getLocation() {
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

    public void setLocation(BotLocation location) {
        this.currentLocation = location;
    }

    // Геттер и Сеттер для targetLocation
    public BotLocation getTarget() {
        return targetLocation;
    }

    public void setTarget(BotLocation targetLocation) {
        this.targetLocation = targetLocation;
    }

}
