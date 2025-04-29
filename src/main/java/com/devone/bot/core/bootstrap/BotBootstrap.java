package com.devone.bot.core.bootstrap;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.brain.BotBrainTask;
import com.devone.bot.core.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.ServerUtils;

public class BotBootstrap {
    private final Bot bot;
    private final BotTaskManager taskManager;

    private boolean brainStarted = false; // ✅ Флаг, чтобы не добавлять Brain Task несколько раз

    public BotBootstrap(Bot bot) {
        this.bot = bot;
        this.taskManager = new BotTaskManager(bot);

        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.debug("🤖", true, "💥 Запускаем Bootstrap для бота " + bot.getId());

        // Отдельный таймер для сканирования окружения (часто)
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (ServerUtils.isServerStopping())
                return;

            String icon = BotUtils.getActiveTaskIcon(bot);

            BotLogger.debug(icon, true, bot.getId() + " 🛜 Sonar Scan started");
            BotSonar3DTask sonar = new BotSonar3DTask(bot);
            sonar.execute();
            BotLogger.debug(icon, true, bot.getId() + " 🛜 Sonar Scan started");

            BotLogger.debug(icon, true, bot.getId() + " 💻 Navigator calculation started");
            bot.getNavigator().calculate(bot.getBrain().getMemory().getSceneData());
            BotLogger.debug(icon, true, bot.getId() + " 💻 Navigator calculation ended");

        }, 0L, 20L); // каждые 10 тиков = 0.5 сек

        // Отдельный таймер для обработки задач (редко)
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (ServerUtils.isServerStopping())
                return;

            update();

        }, 0L, 10L); // каждые 10 тиков = 0.5 сек
    }

    private void update() {
        if (!taskManager.isEmpty()) {
            brainStarted = false;
            taskManager.updateActiveTask();
        } else {
            if (!brainStarted) {
                BotLogger.debug("💥", true, bot.getId() + " 😴 Бот без задач. Добавляем BotBrainTask.");
                BotTaskManager.push(bot, new BotBrainTask(bot));
                brainStarted = true;
            }
        }
    }

    public BotTaskManager getTaskManager() {
        return taskManager;
    }
}
