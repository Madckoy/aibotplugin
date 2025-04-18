package com.devone.bot.core.bot.brain.logic.task;

public interface IBotTask {
    void    setPaused(boolean paused); // ✅ Можно поставить на паузу
    long    getElapsedTime(); // ✅ Добавлен метод для отображения времени выполнения таска
    void    update();
    void    execute();
    void    stop();
}