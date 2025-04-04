package com.devone.aibot.core.logic.tasks;

public interface IBotTask {
    void    setPaused(boolean paused); // ✅ Можно поставить на паузу
    long    getElapsedTime(); // ✅ Добавлен метод для отображения времени выполнения таска
    void    update();
    void    execute();
    void    stop();
}