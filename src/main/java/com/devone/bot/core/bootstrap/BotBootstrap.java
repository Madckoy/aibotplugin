package com.devone.bot.core.bootstrap;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.storage.BotDataStorage;
import com.devone.bot.core.task.active.brain.BotBrainTask;
import com.devone.bot.core.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.task.active.sonar.params.BotSonar3DTaskParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.BotServerUtils;
import com.devone.bot.core.web.bluemap.BlueMapMarkers;

public class BotBootstrap {

    private final Bot bot;
    private final BotTaskManager taskManager;
    private boolean brainStarted = false;

    public BotBootstrap(Bot bot) {
        this.bot = bot;
        this.taskManager = new BotTaskManager(bot);
        scheduleExternalWrappers();
        startFastLogicLoop();
    }

    // 🔁 Быстрая логика (мозг и задачи)
    private void startFastLogicLoop() {
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;

            if (!taskManager.isEmpty()) {
                brainStarted = false;
                taskManager.updateActiveTask(); // Обновляет текущую задачу
            } else if (!brainStarted) {
                BotLogger.debug("🧠", bot.isLogged(), bot.getId() + " 💤 Без задач. Добавляем BotBrainTask.");
                BotTaskManager.push(bot, new BotBrainTask(bot));
                brainStarted = true;
            }

        }, 0L, BotConstants.TICKS_FAST_UPDATE);
    }

    // 🕓 Медленные таймерные задачи
    private void scheduleExternalWrappers() {
        scheduleSceneScanLoop();
        scheduleBlueMapLoop();
        scheduleMemorySaveLoop();
        scheduleMemoryCleanupLoop();
    }

    private void scheduleSceneScanLoop() {
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;
            if (bot.getNavigator().isCalculating()) return;

            String icon = BotUtils.getActiveTaskIcon(bot);
            BotLogger.debug(icon, bot.isLogged(), bot.getId() + " 🛜 Sonar Scan started");

            int radius = BotConstants.DEFAULT_SCAN_RADIUS;
            Integer fromMemory = BotMemoryV2Utils.readValueTyped(bot,
                    BotMemoryPartition.PartitionKey.NAVIGATION,
                    BotMemoryItem.ItemKey.SCAN_RADIUS, Integer.class);
            if (fromMemory != null) radius = fromMemory;

            BotSonar3DTask task = new BotSonar3DTask(bot);
            BotSonar3DTaskParams params = task.getParams();
            params.setRadius(radius);
            task.setParams(params);
            task.execute();

        }, 0L, BotConstants.TICKS_NAVIGATION_UPDATE);
    }

    private void scheduleBlueMapLoop() {
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;

            BlueMapMarkers markers = AIBotPlugin.getInstance().getBotManager().getBlueMapMarkers();
            if (markers != null) {
                markers.updateAllMarkers();
            }

        }, 0L, BotConstants.TICKS_BLUEMAP_UPDATE);
    }

    private void scheduleMemorySaveLoop() {
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;

            BotDataStorage.saveBotData(bot);

        }, 0L, BotConstants.TICKS_MEMORY_SAVE);
    }

    private void scheduleMemoryCleanupLoop() {
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;

            long ttlMillis = 60 * 60 * 1000; // 60 минут
            int removed = BotMemoryV2Utils.cleanupVisited(bot, ttlMillis);
            if (removed > 0) {
                BotLogger.debug("🧠", bot.isLogged(), bot.getId() + " 🧹 Auto-removed " + removed + " visited entries");
            }

        }, 20L, BotConstants.TICKS_MEMORY_CLEANUP);
    }

    public BotTaskManager getTaskManager() {
        return taskManager;
    }
}
