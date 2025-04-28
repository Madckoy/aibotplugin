package com.devone.bot.core.task.passive;

import java.util.Stack;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.logger.BotLifecycleLogger;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTaskManager {

    private final Stack<BotTask<?>> taskStack = new Stack<>();
    // 👇 ДОБАВИМ ВВЕРХУ

    private final Stack<BotTask<?>> reactiveStack = new Stack<>();
    private BotTask<?> currentReactiveRoot = null;

    public boolean isInReactiveMode() {
        return !reactiveStack.isEmpty();
    }

    private final Bot bot;

    private BotReactivityController controller;

    public Bot getBot() {
        return bot;
    }

    public BotReactivityController getController() {
        return controller;
    }

    public void setController(BotReactivityController controller) {
        this.controller = controller;
    }

    public BotTaskManager(Bot bot) {
        this.bot = bot;
        controller = new BotReactivityController(this);
    }

    // Метод теперь работает с обобщённым типом T
    public <T extends BotTaskParams> void pushTask(BotTask<T> task) {
        Stack<BotTask<?>> stack = task.isReactive() ? reactiveStack : taskStack;

        if (!stack.isEmpty()) {
            BotTask<?> currentTask = stack.peek();
            currentTask.setPause(true); // Ставим текущую задачу на паузу
            if (currentTask.isDeffered()) {
                currentTask.setDeffered(false); // Cнимам флажок отложенности т.к пришла следующая таска
            }
        }

        stack.push(task);
        task.setInjected(true); // сообщаем задаче что она добавилась в общий стек

        // если это первый реактивный — запоминаем
        if (task.isReactive() && currentReactiveRoot == null) {
            currentReactiveRoot = task;
        }

        BotLogger.debug("🤖", true,
                bot.getId() + " ➕ Добавлена задача: " + task.getIcon() + " " + task.getClass().getSimpleName());
    }

    public void popTask() {
        Stack<BotTask<?>> stack = isInReactiveMode() ? reactiveStack : taskStack;

        if (!stack.isEmpty()) {
            BotLifecycleLogger.write(this.bot);
            BotTask<?> removed = stack.pop();

            BotLogger.debug("🤖", true, bot.getId() + " ➖ Удалена задача: " + removed.getClass().getSimpleName());

            if (removed == currentReactiveRoot) {
                currentReactiveRoot = null; // реактивная сессия завершена
            }

            if (!stack.isEmpty()) {
                stack.peek().setPause(false);
            }
        }
    }

    public BotTask<?> getActiveTask() {
        if (isInReactiveMode() && !reactiveStack.isEmpty()) {
            return reactiveStack.peek();
        } else if (!taskStack.isEmpty()) {
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
            BotLogger.debug("🤖", true, bot.getId()     + " 🟢 Activate task: " + currentTask.getIcon() + " "
                    + currentTask.getClass().getSimpleName());

            if (currentTask.isDone()) {
                BotLogger.debug("🤖", true, bot.getId() + " ⭕ Deactivating task: " + currentTask.getIcon() + " "
                        + currentTask.getClass().getSimpleName());
                popTask();                        
            
            } else {
                BotLogger.debug("🤖", true, bot.getId() + " 🔵 Updating task: " + currentTask.getIcon() + " "
                        + currentTask.getClass().getSimpleName());

                currentTask.update();        
            }
        }
    }

    public void clearTasks() {
        while (!reactiveStack.isEmpty()) {
            BotTask<?> removedTask = reactiveStack.pop();
            removedTask.stop();
            BotLogger.debug("🤖", true, bot.getId()      + " ⚫ Удалена реактивная задача: " + removedTask.getIcon() + " "
                    + removedTask.getClass().getSimpleName());
        }

        currentReactiveRoot = null;

        while (!taskStack.isEmpty()) {
            BotTask<?> removedTask = taskStack.pop();
            removedTask.stop();
            BotLogger.debug("🤖", true, bot.getId()     + " ⚫ Удалена задача: " + removedTask.getIcon() + " "
                    + removedTask.getClass().getSimpleName());
        }
    }

    public Stack<BotTask<?>> getReactiveStack() {
        return reactiveStack;
    }

    public static void push(Bot bot, BotTask<?> task) {
        task.setReactive(task.isReactive()); // не переопределяем, если уже выставлено

        if (task.isDeffered()) {
            task.setPause(true);
        }

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

            BotLogger.debug("🤖", true, bot.getId() + " Task Info: " + task.getIcon() + " | " + task.getObjective());

            sb.append(task != null ? task.getIcon() : "?");
            if (i < getTaskStack().size() - 1) {
                sb.append(" ➜ ");
            }
        }
        return sb.toString();
    }

}
