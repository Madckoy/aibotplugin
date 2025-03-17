package com.devone.aibot.core.logic;

import org.bukkit.Bukkit;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.BotTaskIdle;
import com.devone.aibot.core.logic.tasks.BotTaskStackManager;
import com.devone.aibot.utils.BotLogger;

public class BotLifeCycle {
    private final Bot bot;
    private final BotTaskStackManager taskStackManager;
    private boolean idleAdded = false; // ✅ Флаг, чтобы не добавлять IdleActivity несколько раз

    public BotLifeCycle(Bot bot) {
        this.bot = bot;
        this.taskStackManager = new BotTaskStackManager(bot);

        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.info("🎲 Запускаем LifeCycle для бота " + bot.getId());

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {

            update();

        }, 0L, 2L); // 2 тика = 0.1 секунды (в 10 раз быстрее, чем было!)
    }

    public void update() {
        if (Bukkit.getServer().isStopping()) return;

        if (!taskStackManager.isEmpty()) {
            idleAdded = false; // ✅ Если есть активность, сбрасываем флаг
            taskStackManager.updateActiveTask();
        } else {
            if (!idleAdded) {
                BotLogger.info("😴 Бот " + bot.getId() + " Без задач. Добавляем IdleTask.");
                taskStackManager.pushTask(new BotTaskIdle(bot));
                idleAdded = true; // ✅ Ставим флаг, что IdleActivity уже добавлена
            }
        }
    }

    public BotTaskStackManager getTaskStackManager() {
        return taskStackManager;
    }
}
