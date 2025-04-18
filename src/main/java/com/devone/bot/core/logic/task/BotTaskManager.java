package com.devone.bot.core.logic.task;

import java.util.Stack;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.logger.BotLifecycleLogger;
import com.devone.bot.utils.logger.BotLogger;

public class BotTaskManager {

    private final Stack<BotTask<?>> taskStack = new Stack<>();

    private final Bot bot;

    public BotTaskManager(Bot bot) {
        this.bot = bot;
    }

    // Метод теперь работает с обобщённым типом T
    public <T extends BotTaskParams> void pushTask(BotTask<T> task) {
        if (!taskStack.isEmpty()) {
            BotTask<?> currentTask = taskStack.peek();
            currentTask.setPaused(true); // Ставим текущую задачу на паузу
        }

        taskStack.push(task);

        BotLogger.debug("✚", true, "Добавлена задача: " + task.getClass().getSimpleName());
    }

    public void popTask() {
        if (!taskStack.isEmpty()) {

            BotLifecycleLogger.write(this.bot);

            BotLogger.debug("➖", true, "Удалена задача: " + taskStack.peek().getClass().getSimpleName());
            taskStack.pop();

            // ✅ Если осталась активность, снимаем с неё паузу
            if (!taskStack.isEmpty()) {
                taskStack.peek().setPaused(false);
            }
        }
    }

    public BotTask<?> getActiveTask() {
        if (!taskStack.isEmpty()) {
            return  taskStack.peek();

        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return taskStack.isEmpty();
    }

    public boolean isTaskActive(Class<? extends BotTask<?>> taskClass) {
        for (BotTask<?> task : taskStack) {
            if (task.getClass().equals(taskClass) && !task.isDone()) {
                return true;
            }
        }
        return false;
    }

    public Stack<BotTask<?>> getTaskStack() {
        return taskStack;
    }


    public void updateActiveTask() {
        if (!taskStack.isEmpty()) {

            BotTask<?> currentTask = taskStack.peek();

            BotLogger.debug("✨", true, "Active task: " + currentTask.getClass().getSimpleName());
    
            if (currentTask.isDone()) {
                popTask();
                BotLogger.debug("✨", true, "Deactivating task: " + currentTask.getClass().getSimpleName());
            } else {
                BotLogger.debug("✨", true, "Updating task: " + currentTask.getClass().getSimpleName());
                currentTask.update();
            }
        }
    }
    
    // ✅ Функция для удаления всех задач с логированием
    public void clearTasks() {
        while (!taskStack.isEmpty()) {
            BotTask<?> removedTask = taskStack.pop();
            removedTask.stop();
            BotLogger.debug("❌", true, "Удалена задача: " + removedTask.getClass().getSimpleName());
        }
    }

}
