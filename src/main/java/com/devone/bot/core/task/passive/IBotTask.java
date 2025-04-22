package com.devone.bot.core.task.passive;

public interface IBotTask {
    void    setPause(boolean paused); // ✅ Можно поставить на паузу
    long    getElapsedTime(); // ✅ Добавлен метод для отображения времени выполнения таска
    void    update();
    void    execute();
    void    stop();
}