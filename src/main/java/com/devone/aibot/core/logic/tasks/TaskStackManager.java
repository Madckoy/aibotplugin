package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.utils.BotLogger;

import java.util.Stack;

public class TaskStackManager {
    private final Stack<BotTask> taskStack = new Stack<>();

    public void pushTask(BotTask task) {
        if (!taskStack.isEmpty()) {
            BotTask currentActivity = taskStack.peek();
            currentActivity.setPaused(true); // ✅ Ставим текущую активность на паузу
        }

        taskStack.push(task);
        BotLogger.debug("📌 Добавлена активность: " + task.getClass().getSimpleName());
    }

    public void popActivity() {
        if (!taskStack.isEmpty()) {
            BotLogger.debug("🗑 Удаляем активность: " + taskStack.peek().getClass().getSimpleName());
            taskStack.pop();

            // ✅ Если осталась активность, снимаем с неё паузу
            if (!taskStack.isEmpty()) {
                taskStack.peek().setPaused(false);
            }
        }
    }

    public BotTask getCurrentTask() {
        if (!taskStack.isEmpty()) {
            return  taskStack.peek();
        } else {
            return null;
        }
    }

    public void updateCurrentTask() {
        if (!taskStack.isEmpty()) {
            BotTask currentTask = taskStack.peek();
            if (currentTask.isDone()) {
                popActivity();
            } else {
                currentTask.update();
            }
        }
    }

    public boolean isEmpty() {
        return taskStack.isEmpty();
    }

    public void clearTasks() {
        taskStack.clear();   
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

}
