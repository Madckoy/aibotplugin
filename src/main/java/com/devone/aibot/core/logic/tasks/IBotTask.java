package com.devone.aibot.core.logic.tasks;

public interface IBotTask {
    boolean isEnabled();
    boolean isLogged();
    boolean isDone();
    void    setPaused(boolean paused); // ✅ Можно поставить на паузу
    String  getName();
    long    getElapsedTime(); // ✅ Добавлен метод для отображения времени выполнения таска
    void    update();
}