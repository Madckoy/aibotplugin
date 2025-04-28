package com.devone.bot.core.task.passive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;

public abstract class BotReactiveTaskContainer<T extends BotTaskParams> extends BotTaskAutoParams<T> {

    protected List<BotTask<?>> subtasks = new ArrayList<>();
    protected boolean injected = false;

    public BotReactiveTaskContainer(Bot bot, Class<T> paramClass) {
        super(bot, null, paramClass);
        setReactive(true); // Включаем реактивный режим
        setIcon("📦");
        setObjective("Reactive container for tasks");
        setDeffered(true);
    }

    @Override
    public void execute() {
        if (injected) {
            BotLogger.debug(getIcon(), true,
                    bot.getId() + " ⚡ Запущен реактивный контейнер: " + this.getClass().getSimpleName());

            setPause(true);        
            if(isDeffered()==true) {

                subtasks = enqueue(bot); // 📦 добавляем задачи

                if(subtasks == null) {
                    stop();
                    return;
                }
            }

            List<BotTask<?>> reversed = new ArrayList<>(subtasks);
            Collections.reverse(reversed);

            for (BotTask<?> task : reversed) {
                BotLogger.debug(getIcon(), true,
                        bot.getId() + " ➕ Запуск подзадачи: " + task.getClass().getSimpleName());
                task.setReactive(true);
                bot.getTaskManager().pushTask(task);
            }

            return;

        } else {
            if(subtasks!=null) {
                boolean allDone = subtasks.stream().allMatch(BotTask::isDone);

                if (allDone) {
                    BotLogger.debug(getIcon(), true, bot.getId() + " ✅ Все подзадачи завершены. Контейнер закрывается: "
                                + this.getClass().getSimpleName());
                    stop();
                }
            }
        }
    }

    /**
     * Автоматическая генерация подзадач
     */
    protected List<BotTask<?>> enqueue(Bot bot) {
        // По умолчанию ничего не делает — можно переопределить
        return subtasks;
    }

    @Override
    public void stop() {
        setDeffered(false);
        setReactive(false);
        BotLogger.debug(getIcon(), true, bot.getId() + " 🔚 Контейнер снят: " + this.getClass().getSimpleName());
        super.stop();
    }
}
