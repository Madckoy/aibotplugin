package com.devone.bot.core.bot.task.passive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
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
        if (!started) {
            BotLogger.debug(getIcon(), true,
                    bot.getId() + " ⚡ Запущен реактивный контейнер: " + this.getClass().getSimpleName());

            enqueue(bot); // 📦 добавляем задачи

            List<BotTask<?>> reversed = new ArrayList<>(subtasks);
            Collections.reverse(reversed);

            for (BotTask<?> task : reversed) {
                BotLogger.debug(getIcon(), true,
                        bot.getId() + " ➕ Запуск подзадачи: " + task.getClass().getSimpleName());
                task.setReactive(true);
                bot.getTaskManager().pushTask(task);
            }

            started = true;

            return; // ⏳ ждем выполнения подзадач
        }

        // ✅ Проверяем: завершены ли все подзадачи
        boolean allDone = subtasks.stream().allMatch(BotTask::isDone);

        if (allDone) {
            BotLogger.debug(getIcon(), true,
                    bot.getId() + " 🧹 Все подзадачи завершены. Контейнер закрывается: "
                            + this.getClass().getSimpleName());
            stop();
        }
    }

    /**
     * Добавление подзадачи вручную до запуска
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
     * Автоматическая генерация подзадач
     */
    protected void enqueue(Bot bot) {
        // По умолчанию ничего не делает — можно переопределить
    }

    @Override
    public void stop() {
        done = true;

        super.stop();
        BotLogger.debug(getIcon(), true, bot.getId() + " 🔚 Контейнер снят: " + this.getClass().getSimpleName());
    }
}
