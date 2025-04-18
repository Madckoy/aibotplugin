package com.devone.bot.core.logic.lifecycle;

import org.bukkit.Bukkit;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTaskManager;
import com.devone.bot.core.logic.task.brain.BotBrainTask;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.ServerUtils;
import com.devone.bot.plugin.AIBotPlugin;

public class BotLifeCycle {
    private final Bot bot;
    private final BotTaskManager taskStackManager;
    private boolean idleAdded = false; // ✅ Флаг, чтобы не добавлять IdleActivity несколько раз

    public BotLifeCycle(Bot bot) {
        this.bot = bot;
        this.taskStackManager = new BotTaskManager(bot);

        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.debug("🎲", true, "Запускаем LifeCycle для бота " + bot.getId());

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {

            update();

        }, 0L, 2L); // 2 тика = 0.1 секунды (в 10 раз быстрее, чем было!)
    }

    public void update() {
        if (ServerUtils.isServerStopping()) return;

        if (!taskStackManager.isEmpty()) {
            idleAdded = false; // ✅ Если есть активность, сбрасываем флаг
            taskStackManager.updateActiveTask();
        } else {
            if (!idleAdded) {
                BotLogger.debug("😴", true, "Бот " + bot.getId() + " Без задач. Добавляем IdleTask.");
                
                taskStackManager.pushTask(new BotBrainTask(bot));
                
                idleAdded = true; // ✅ Ставим флаг, что IdleActivity уже добавлена
            }
        }
    }

    public BotTaskManager getTaskStackManager() {
        return taskStackManager;
    }
}
