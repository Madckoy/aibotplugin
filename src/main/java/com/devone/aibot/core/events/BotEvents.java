package com.devone.aibot.core.events;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotMoveTask;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.utils.BotLogger;

import io.papermc.paper.event.entity.EntityMoveEvent;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.HashMap;
import java.util.UUID;

public class BotEvents implements Listener {

    private final BotManager botManager;
    private final HashMap<UUID, Location> lastLocations = new HashMap<>();
    private final HashMap<UUID, Long> lastMoveTimes = new HashMap<>();
    private static final long STUCK_TIME = 5000; // 5 секунд застревания

    public BotEvents(BotManager botManager) {
        this.botManager = botManager;
    }


    /**
     * Отслеживание движения бота
     */
    @EventHandler
    public void onBotMove(EntityMoveEvent event) {
        UUID botId = event.getEntity().getUniqueId();

        Bot bot = botManager.getBot(botId.toString());
        if (bot == null || bot.getNPCEntity() == null) return; // ✅ Проверяем null перед использованием

        Location currentLocation = event.getEntity().getLocation();

        if (lastLocations.containsKey(botId)) {
            Location lastLocation = lastLocations.get(botId);
            long lastMoveTime = lastMoveTimes.getOrDefault(botId, System.currentTimeMillis());

            // Проверяем, застрял ли бот
            if (currentLocation.equals(lastLocation) && (System.currentTimeMillis() - lastMoveTime > STUCK_TIME)) {
                BotLogger.info("[onBotMove]: "+ event.getEntity().getName() + " ⚠️ Бот застрял!");

                // Получаем последнюю активную задачу бота
                BotTask activeTask = botManager.getBot(botId.toString()).getCurrentTask();
                
                if (activeTask instanceof BotMoveTask moveTask) {
                    moveTask.handleStuck(); // Сообщаем задаче, что бот застрял
                }
            }
        }

        lastLocations.put(botId, currentLocation);
        lastMoveTimes.put(botId, System.currentTimeMillis());
    }
}
