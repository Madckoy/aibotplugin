package com.devone.bot.core.task.passive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;

public abstract class BotSequenceContainer<T extends BotTaskParams> extends BotTaskAutoParams<T> {

    protected List<BotTask<?>> subtasks = new ArrayList<>();
    protected boolean injected = false;

    public BotSequenceContainer(Bot bot, Class<T> paramClass) {
        super(bot, null, paramClass);
        setIcon("<");
        setObjective("Container for tasks");
        setDeffered(true);
    }

    @Override
    public void execute() {

            BotLogger.debug(getIcon(), isLogged(),
                    bot.getId() + " 🔣 Запущен контейнер: " + this.getClass().getSimpleName());
      
            if(isDeffered()==true) {
                BotLogger.debug(getIcon(), isLogged(),
                    bot.getId() + " ➕  Добавляем вложенные задачи в стек");

                subtasks = enqueue(bot); // 📦 добавляем задачи

                if(subtasks == null) {
                    stop();
                    return;
                } else {
                    BotLogger.debug(getIcon(), isLogged(),
                    bot.getId() + " ☑️ Добавлены задачи: " + subtasks);
                }

                List<BotTask<?>> reversed = new ArrayList<>(subtasks);
                Collections.reverse(reversed);
                
                bot.getTaskManager().wait(true); // stop updating the stack

                for (BotTask<?> task : reversed) {
                    BotLogger.debug(getIcon(), isLogged(),
                            bot.getId() + " 🔜 Запуск подзадачи: " + task.getClass().getSimpleName());

                    bot.getTaskManager().wait(true); 
                    bot.getTaskManager().pushTask(task);
                }

                bot.getTaskManager().wait(false); // continue updating the stack
                setDeffered(false);
            }

            stop();
            /*
            if(subtasks!=null) {
                boolean allDone = subtasks.stream().allMatch(BotTask::isDone);

                if (allDone) {
                    BotLogger.debug(getIcon(), isLogged(), bot.getId() + " 🔚 Все подзадачи завершены. Контейнер закрывается: "
                                + this.getClass().getSimpleName());
                    stop();
                }

            } else {
               stop();     
            }
            */    
            return;
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
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " 🔚 Контейнер снят: " + this.getClass().getSimpleName());
        super.stop();
    }
}
