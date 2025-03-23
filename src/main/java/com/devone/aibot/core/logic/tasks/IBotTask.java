package com.devone.aibot.core.logic.tasks;

public interface IBotTask {
    void update();
    boolean isDone();
    void setPaused(boolean paused); // ✅ Можно поставить на паузу
    Object configure(Object... params);
    String getName();
    long getElapsedTime(); // ✅ Добавлен метод для отображения времени выполнения таска
}