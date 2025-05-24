package com.devone.bot.core.bootstrap;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.brain.BotBrainTask;
import com.devone.bot.core.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.task.active.sonar.params.BotSonar3DTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.BotServerUtils;
import com.devone.bot.core.web.bluemap.BlueMapMarkers;
import com.devone.bot.core.storage.BotDataStorage;

public class BotBootstrap {

    private final Bot bot;
    private final BotTaskManager taskManager;
    private boolean brainStarted  = false;

    public BotBootstrap(Bot bot) {
        this.bot = bot;
        this.taskManager = new BotTaskManager(bot);
        startLifeCycle();
    }

    private void startLifeCycle() {
        BotLogger.debug("🤖", true, "💥 Запускаем Bootstrap для бота " + bot.getId());

        // ⛏️ Обновление навигации и сканирования
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;
            if(bot.getNavigator().isCalculating()) return;

            String icon = BotUtils.getActiveTaskIcon(bot);           
            BotLogger.debug(icon, true, bot.getId() + " 🛜 Sonar Scan started");
            BotSonar3DTaskParams params = new BotSonar3DTaskParams();
            int radius = BotConstants.DEFAULT_SCAN_RADIUS;
            Integer scanRadius = (Integer) BotMemoryV2Utils.readMemoryValue(bot, "navigation", "scanRadius");               
            if(scanRadius!=null) {
                radius = scanRadius.intValue();
            }
            params.setRadius(radius);
            BotSonar3DTask task = new BotSonar3DTask(bot);
            task.setParams(params);
            task.execute();
            
        }, 0L, BotConstants.TICKS_NAVIGATION_UPDATE);

        // 🧠 Обработка задач (редко)
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;
            update();
        }, 0L, BotConstants.TICKS_TASK_UPDATE);

        // 🗺️ Обновление POI на BlueMap
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;
            
            BlueMapMarkers markers = AIBotPlugin.getInstance().getBotManager().getBlueMapMarkers();
            if(markers!=null) {
                markers.updateAllMarkers();
            }

        }, 0L, BotConstants.TICKS_BLUEMAP_UPDATE);

        // 💾 Сохранение памяти и инвентаря на диск
        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (BotServerUtils.isServerStopping()) return;
            BotDataStorage.saveBotData(bot);
        }, 0L, BotConstants.TICKS_MEMORY_SAVE);

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            long ttlMillis = 60 * 60 * 1000; // 60 минут
            int removed = BotMemoryV2Utils.cleanupVisited(bot, ttlMillis);
            if (removed > 0) {
                BotLogger.debug("🧠", true, bot.getId() + " 🧹 Auto-removed " + removed + " visited entries");
            }
        }, 20L, 600L); // каждые 30 секунд (600 ticks)
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
