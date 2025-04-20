
package com.devone.bot.core.bot.task.passive;

import com.devone.bot.core.bot.Bot;

public class BotReactivityController {
    private BotTask<?> currentReactiveTask = null;
    private BotTaskManager manager;

    public BotReactivityController(BotTaskManager mgr){
        this.manager = mgr;
    }

    public boolean canReact(Bot bot) {
        return currentReactiveTask == null;
    }

    public void startReactiveTask(Bot bot, BotTask<?> task) {
        this.currentReactiveTask = task;
        manager.pushTask(task);
    }

    public void endReactiveTask(BotTask<?> task) {
        if (this.currentReactiveTask == task) {
            this.currentReactiveTask = null;
        }
    }

    public boolean isActiveReactiveTask(BotTask<?> task) {
        return this.currentReactiveTask == task;
    }
}