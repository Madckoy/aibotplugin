package com.devone.bot.core.task.passive;

import java.util.Stack;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.logger.BotLifecycleLogger;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTaskManager {

    private final Stack<BotTask<?>> taskStack = new Stack<>();

    //private final Stack<BotTask<?>> reactiveStack = new Stack<>();
    private BotTask<?> currentReactiveRoot = null;

    private boolean waiting = false;

    private Bot bot;

    public Bot getBot() {
        return bot;
    }

    public BotTaskManager(Bot bot) {
        this.bot = bot;
    }

    // Метод теперь работает с обобщённым типом T
    public <T extends BotTaskParams> void pushTask(BotTask<T> task) {

        if (!taskStack.isEmpty()) {
            BotTask<?> currentTask = taskStack.peek();
            currentTask.setPause(true); // Ставим текущую задачу на паузу
        }

        taskStack.push(task);
        task.setInjected(true); // сообщаем задаче что она добавилась в общий стек

        // если это первый реактивный — запоминаем
        if (task.isReactive() && currentReactiveRoot == null) {
            currentReactiveRoot = task;
        }

        BotLogger.debug("🤖", task.isLogged(),
                bot.getId() + " ➕ Добавлена задача: " + task.getIcon() + " " + task.getClass().getSimpleName());
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void wait(boolean w) {
        this.waiting = w;
    }
    
    public void popTask() {
        if (!taskStack.isEmpty()) {
            BotLifecycleLogger.write(this.bot);
            BotTask<?> removed = taskStack.pop();

            BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " ➖ Удалена задача: " + removed.getClass().getSimpleName());

            if (removed == currentReactiveRoot) {
                currentReactiveRoot = null; // реактивная сессия завершена
            }

            if (!taskStack.isEmpty()) {
                taskStack.peek().setPause(false);
            }
        }
    }

    public BotTask<?> getActiveTask() {
        if (!taskStack.isEmpty()) {
            return taskStack.peek();
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
        BotTask<?> currentTask = getActiveTask();
        if (currentTask != null) {
            BotLogger.debug("🤖", currentTask.isLogged(), bot.getId()     + " 🟢 Activate task: " + currentTask.getIcon() + " "
                    + currentTask.getClass().getSimpleName());

            if (currentTask.isDone()) {
                BotLogger.debug("🤖", currentTask.isLogged(), bot.getId() + " ⭕ Deactivating task: " + currentTask.getIcon() + " "
                        + currentTask.getClass().getSimpleName());
                popTask();                        
            
            } else {
                BotLogger.debug("🤖", currentTask.isLogged(), bot.getId() + " 🔵 Updating task: " + currentTask.getIcon() + " "
                        + currentTask.getClass().getSimpleName());

                if(isWaiting()==false) {
                    currentTask.update();
                }        
            }
        }
    }

    public void clearTasks() {

        currentReactiveRoot = null;

        while (!taskStack.isEmpty()) {
            BotTask<?> removedTask = taskStack.pop();
            removedTask.stop();
            BotLogger.debug("🤖",removedTask.isLogged(), bot.getId()     + " ⚫ Удалена задача: " + removedTask.getIcon() + " "
                    + removedTask.getClass().getSimpleName());
        }
    }

    public static void push(Bot bot, BotTask<?> task) {
        task.setReactive(task.isReactive()); // не переопределяем, если уже выставлено
        bot.getTaskManager().pushTask(task);
    }

    public static void clear(Bot bot) {
        bot.getBootstrap().getTaskManager().clearTasks();
    }

    public String getQueueIcons() {
        if (this == null || getTaskStack() == null || getTaskStack().isEmpty()) {
            return "N/A";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getTaskStack().size(); i++) {
            BotTask<?> task = getTaskStack().get(i);

            BotLogger.debug("🤖", task.isLogged(), bot.getId() + " Task Info: " + task.getIcon() + " | " + task.getObjective());

            sb.append(task != null ? task.getIcon() : "?");
            if (i < getTaskStack().size() - 1) {
                sb.append(" ➜ ");
            }
        }
        return sb.toString();
    }

}
