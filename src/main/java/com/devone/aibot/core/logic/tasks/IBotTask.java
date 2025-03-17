package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;

public interface IBotTask {
    void update();
    boolean isDone();
    void setPaused(boolean paused); // ✅ Можно поставить на паузу
    void configure(Object... params);
    String getName();
    Location getTargetLocation();
    long getElapsedTime(); // ✅ Добавлен метод для отображения времени выполнения таска
}