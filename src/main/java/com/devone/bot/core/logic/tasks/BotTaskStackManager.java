package com.devone.bot.core.logic.tasks;

import java.util.Stack;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.utils.logger.BotLifecycleLogger;
import com.devone.bot.utils.logger.BotLogger;

public class BotTaskStackManager {
    private final Stack<BotTask> taskStack = new Stack<>();
    private final Bot bot;

    public BotTaskStackManager(Bot bot) {
        this.bot = bot;
    }

    public void pushTask(BotTask task) {
        if (!taskStack.isEmpty()) {
            BotTask currentTask = taskStack.peek();
            currentTask.setPaused(true); // ✅ Ставим текущую активность на паузу
        }

        taskStack.push(task);
        BotLogger.info(true, "✚ Добавлена задача: " + task.getClass().getSimpleName() + "[ " + task.getUUID()+" ]");
    }

    public void popTask() {
        if (!taskStack.isEmpty()) {

            BotLifecycleLogger.write(this.bot);

            BotLogger.info(true, "➖ Удалена задача: " + taskStack.peek().getClass().getSimpleName());
            taskStack.pop();

            // ✅ Если осталась активность, снимаем с неё паузу
            if (!taskStack.isEmpty()) {
                taskStack.peek().setPaused(false);
            }
        }
    }

    public BotTask getActiveTask() {
        if (!taskStack.isEmpty()) {
            return  taskStack.peek();

        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return taskStack.isEmpty();
    }

    public boolean isTaskActive(Class<? extends BotTask> taskClass) {
        for (BotTask task : taskStack) {
            if (task.getClass().equals(taskClass) && !task.isDone()) {
                return true;
            }
        }
        return false;
    }

    public Stack<BotTask> getTaskStack() {
        return taskStack;
    }


    public void updateActiveTask() {
        if (!taskStack.isEmpty()) {

            BotTask currentTask = taskStack.peek();

            BotLogger.info(true, "✨ Active task: " + currentTask.getClass().getSimpleName() + " [" +currentTask.getUUID() +"]");
    
            // 🛑 Если у бота нет NPCEntity, удаляем ВСЕ задачи
            //if (bot.getNPCEntity() == null) {
            //    BotLogger.info(bot.getId() + " ❌ Ошибка: NPCEntity == null! Очищаю задачи...");
            //    clearTasks();
            //    return;
            //}
    
            if (currentTask.isDone()) {
                popTask();
                BotLogger.info(true, "✨ Deactivating task: " + currentTask.getClass().getSimpleName() + " [" +currentTask.getUUID() +"]");
            } else {
                BotLogger.info(true, "✨ Updating task: " + currentTask.getClass().getSimpleName() + " [" +currentTask.getUUID() +"]");
                currentTask.update();
            }
        }
    }
    
    // ✅ Функция для удаления всех задач с логированием
    public void clearTasks() {
        while (!taskStack.isEmpty()) {
            BotTask removedTask = taskStack.pop();
            BotLogger.info(true, "❌ Удалена задача: " + removedTask.getClass().getSimpleName());
        }
    }

}
