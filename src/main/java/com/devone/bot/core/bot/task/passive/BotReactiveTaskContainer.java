package com.devone.bot.core.bot.task.passive;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.brain.BotBrainTask;
import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;

public abstract class BotReactiveTaskContainer<T extends BotTaskParams> extends BotTaskAutoParams<T> {

    private boolean started = false;

    public BotReactiveTaskContainer(Bot bot, Class<T> paramClass) {
        super(bot, null, paramClass);
        setReactive(true); // активирует реактивный режим
    }

    @Override
    public void execute() {
        if (!started) {
            BotLogger.debug(getIcon(), true, bot.getId() + " ⚡ Запущен реактивный контейнер: " + this.getClass().getSimpleName());

            BotTask<?> active = bot.getActiveTask();

            // 💡 Если текущая задача — НЕ мозг, убираем её
            if (active != null && !(active instanceof BotBrainTask)) {
                BotLogger.debug(icon, true, bot.getId() + " 🔁 Завершаем активную задачу: " + active.getClass().getSimpleName());
                active.stop();
            }
   
            enqueue(bot); // 👈 Реально вбрасываем задачи
            started = true;

            BotLogger.debug(getIcon(), true, bot.getId() + " ✅ Контейнер завершает себя: " + this.getClass().getSimpleName());
            stop();
        }
    }

    @Override
    public void stop() {
        super.stop(); // done = true

        BotLogger.debug(getIcon(), true, bot.getId() + " 🔚 Контейнер снят: " + this.getClass().getSimpleName());

        // тут можешь логгировать реактивный стек, если нужно
        bot.reactiveTaskStop(this);
    }

    protected abstract void enqueue(Bot bot);
}
