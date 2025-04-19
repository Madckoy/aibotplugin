package com.devone.bot.core.bot.bootstrap;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.brain.BotBrainTask;
import com.devone.bot.core.bot.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.ServerUtils;

public class BotBootstrap {
    private final Bot bot;
    private final BotTaskManager taskStackManager;

    private boolean brainStarted = false; // ✅ Флаг, чтобы не добавлять Brain Task несколько раз

    public BotBootstrap(Bot bot) {
        this.bot = bot;
        this.taskStackManager = new BotTaskManager(bot);

        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.debug("💥", true, " 💥 Запускаем Bootstrap для бота " + bot.getId());

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {

            update();

        }, 0L, 2L); // 2 тика = 0.1 секунды (в 10 раз быстрее, чем было!)
    }

    public void update() {
        if (ServerUtils.isServerStopping()) return;

        if (!taskStackManager.isEmpty()) {
        
            brainStarted = false; // ✅ Если есть активность, сбрасываем флаг
        
            taskStackManager.updateActiveTask();
        
        } else {
            if (!brainStarted) {
                BotLogger.debug("💥", true, " 😴 Бот " + bot.getId() + " Без задач. Добавляем BotBrainTask.");
                
                BotUtils.pushTask(bot, new BotBrainTask(bot));
                
                brainStarted = true; // ✅ Ставим флаг, что IdleActivity уже добавлена
            }
        }
    }

    public BotTaskManager getTaskStackManager() {
        return taskStackManager;
    }
}
