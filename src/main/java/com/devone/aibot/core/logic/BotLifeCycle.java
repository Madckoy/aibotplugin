package com.devone.aibot.core.logic;

import org.bukkit.Bukkit;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.BotIdleTask;
import com.devone.aibot.core.logic.tasks.TaskStackManager;
import com.devone.aibot.utils.BotLogger;

public class BotLifeCycle {
    private final Bot bot;
    private final TaskStackManager taskStackManager;
    private boolean idleAdded = false; // ✅ Флаг, чтобы не добавлять IdleActivity несколько раз

    public BotLifeCycle(Bot bot) {
        this.bot = bot;
        this.taskStackManager = new TaskStackManager();

        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.debug("🔄 Запускаем LifeCycle для бота " + bot.getId());

        new Thread(() -> {
            while (true) {
                update();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public void update() {
        if (Bukkit.getServer().isStopping()) return;

        if (!taskStackManager.isEmpty()) {
            idleAdded = false; // ✅ Если есть активность, сбрасываем флаг
            taskStackManager.updateCurrentTask();
        } else {
            if (!idleAdded) {
                BotLogger.debug("😴 Бот " + bot.getId() + " простаивает. Добавляем IdleActivity.");
                taskStackManager.pushTask(new BotIdleTask(bot));
                idleAdded = true; // ✅ Ставим флаг, что IdleActivity уже добавлена
            }
        }
    }

    public TaskStackManager getTaskStackManager() {
        return taskStackManager;
    }
}
