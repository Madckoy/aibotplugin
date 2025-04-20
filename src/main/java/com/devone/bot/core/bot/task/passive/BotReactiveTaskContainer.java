package com.devone.bot.core.bot.task.passive;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;

public abstract class BotReactiveTaskContainer<T extends BotTaskParams> extends BotTaskAutoParams<T> {

    protected final List<BotTask<?>> subtasks = new ArrayList<>();
    protected boolean started = false;

    public BotReactiveTaskContainer(Bot bot, Class<T> paramClass) {
        super(bot, null, paramClass);
        setReactive(true); // Включаем реактивный режим
        setIcon("📦");
        setObjective("Reactive container for tasks");
    }

    @Override
    public void execute() {
        if (started)
            return;

        BotLogger.debug(getIcon(), true,
                bot.getId() + " ⚡ Запущен реактивный контейнер: " + this.getClass().getSimpleName());
        started = true;

        enqueue(bot); // 👈 Добавление кастомных подзадач (если нужно переопределить)

        for (BotTask<?> task : subtasks) {
            BotLogger.debug(getIcon(), true,
                    bot.getId() + " ➕ Запуск реактивной подзадачи: " + task.getClass().getSimpleName());
            task.setReactive(true);
            bot.getTaskManager().pushTask(task);
        }

        BotLogger.debug(getIcon(), true,
                bot.getId() + " ✅ Контейнер завершает себя: " + this.getClass().getSimpleName());
        stop();
    }

    /**
     * Метод для добавления подзадачи вручную
     */
    public BotReactiveTaskContainer<T> add(BotTask<?> task) {
        if (started) {
            BotLogger.debug(getIcon(), true, bot.getId() + " ⚠️ Попытка добавить задачу после старта контейнера: "
                    + task.getClass().getSimpleName());
            return this;
        }
        subtasks.add(task);
        return this;
    }

    /**
     * Переопределить, если нужно автоматически добавить подзадачи
     */
    protected void enqueue(Bot bot) {
        // По умолчанию ничего не делает
    }

    @Override
    public void stop() {
        super.stop();
        BotLogger.debug(getIcon(), true, bot.getId() + " 🔚 Контейнер снят: " + this.getClass().getSimpleName());
    }
}
