package com.devone.bot.core.bootstrap;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.brain.BotBrainTask;
import com.devone.bot.core.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.ServerUtils;
import com.devone.bot.core.storage.BotDataStorage;

public class BotBootstrap {

    private final Bot bot;
    private final BotTaskManager taskManager;
    private boolean brainStarted = false;

    public BotBootstrap(Bot bot) {
        this.bot = bot;
        this.taskManager = new BotTaskManager(bot);
        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.debug("🤖", true, "💥 Запускаем Bootstrap для бота " + bot.getId());

        // ⛏️ Обновление навигации и сканирования
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (ServerUtils.isServerStopping()) return;

            String icon = BotUtils.getActiveTaskIcon(bot);

            BotLogger.debug(icon, true, bot.getId() + " 🛜 Sonar Scan started");
            new BotSonar3DTask(bot).execute();
            BotLogger.debug(icon, true, bot.getId() + " 💻 Navigator calculation started");
            bot.getNavigator().calculate(bot.getBrain().getSceneData());
            BotLogger.debug(icon, true, bot.getId() + " 💻 Navigator calculation ended");

        }, 0L, BotConstants.TICKS_NAVIGATION_UPDATE);

        // 🧠 Обработка задач (редко)
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (ServerUtils.isServerStopping()) return;
            update();
        }, 0L, BotConstants.TICKS_TASK_UPDATE);

        // 🗺️ Обновление POI на BlueMap
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (ServerUtils.isServerStopping()) return;
            AIBotPlugin.getInstance().getBotManager().getBlueMapMarkers().updateAllMarkers();
        }, 0L, BotConstants.TICKS_BLUEMAP_UPDATE);

        // 💾 Сохранение памяти и инвентаря на диск
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (ServerUtils.isServerStopping()) return;
            BotDataStorage.saveBotData(bot);
        }, 0L, BotConstants.TICKS_MEMORY_SAVE);
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
