package com.devone.bot.core.task.active.hand.attack.listener;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.utils.logger.BotLogger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.HandlerList;

public class BotHandAttackListener implements Listener {

    private final BotHandAttackTask task;

    public BotHandAttackListener(BotHandAttackTask task) {
        this.task = task;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Bot bot = task.getBot();
        
        // Проверяем, что убийца - это наш бот
        if (event.getEntity().getKiller() != null 
            && event.getEntity().getKiller().getUniqueId().equals(bot.getNPCEntity().getUniqueId())) {

            BotMemoryV2Utils.incrementSummaryCounter(bot, "mobsKilled", event.getEntity().getType().name());

            task.stop(); // Завершаем задачу

            BotLogger.debug("💀", true, bot.getId() + " убил моба: " + event.getEntity().getType());
        }
    }

    // Удаляем слушателя
    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
