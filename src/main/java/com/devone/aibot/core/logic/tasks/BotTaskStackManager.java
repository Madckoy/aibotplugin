package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotLifecycleLogCsv;

import java.util.Stack;

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
        BotLogger.info("✚ Добавлена задача: " + task.getClass().getSimpleName());
    }

    public void popTask() {
        if (!taskStack.isEmpty()) {

            BotLifecycleLogCsv.write(this.bot);

            BotLogger.debug("➖ Удалена задача: " + taskStack.peek().getClass().getSimpleName());
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
    
            // 🛑 Если у бота нет NPCEntity, удаляем ВСЕ задачи
            //if (bot.getNPCEntity() == null) {
            //    BotLogger.error(bot.getId() + " ❌ Ошибка: NPCEntity == null! Очищаю задачи...");
            //    clearTasks();
            //    return;
            //}
    
            if (currentTask.isDone()) {
                popTask();
            } else {
                currentTask.update();
            }
        }
    }
    
    // ✅ Функция для удаления всех задач с логированием
    public void clearTasks() {
        while (!taskStack.isEmpty()) {
            BotTask removedTask = taskStack.pop();
            BotLogger.debug("❌ Удалена задача: " + removedTask.getClass().getSimpleName());
        }
    }

}
