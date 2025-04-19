package com.devone.bot.core.bot.task.reactive.sequence;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.reactive.sequence.params.BotReactiveSequenceTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.LinkedList;
import java.util.List;

public class BotReactiveSequenceTask extends BotTaskAutoParams<BotReactiveSequenceTaskParams> {

    private final LinkedList<BotTask<?>> sequence = new LinkedList<>();
    private BotTask<?> currentTask;

    public BotReactiveSequenceTask(Bot bot, List<BotTask<?>> tasks) {
        super(bot, null, BotReactiveSequenceTaskParams.class); // передаём пустой параметр + класс
        this.sequence.addAll(tasks);
        setIcon("📦");
        setObjective("Реактивная цепочка задач");
    }

    @Override
    public void execute() {
        if (currentTask == null || currentTask.isDone()) {
            if (sequence.isEmpty()) {
                BotLogger.debug("📦", isLogging(), bot.getId() + " ✅ Все задачи цепочки выполнены.");
                this.stop(); // всё выполнено
                return;
            }

            // Забираем следующую задачу
            currentTask = sequence.poll();
            BotLogger.debug("📦", isLogging(), bot.getId() + " ▶ Запускаем задачу: " + currentTask.getClass().getSimpleName());
            
            BotUtils.pushTask(bot, currentTask);
        }
    }

    @Override
    public void stop() {
        currentTask = null;
        sequence.clear();
        BotLogger.debug("📦", isLogging(), bot.getId() + " ⛔ Цепочка реактивных задач остановлена.");
        super.stop();
    }
}
