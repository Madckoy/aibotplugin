package com.devone.bot.core.logic.tasks.hand.listeners;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.logic.tasks.hand.BotHandTask;
import com.devone.bot.utils.logger.BotLogger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BotKillListener implements Listener {

    private final BotHandTask task;

    public BotKillListener(BotHandTask task) {
        this.task = task;
    }

    public void onEntityDeath(EntityDeathEvent event) {
        Bot bot = task.getBot();
        
        if (event.getEntity().getKiller() != null
            && event.getEntity().getKiller().getUniqueId().equals(bot.getNPCEntity().getUniqueId())) {
            
            bot.getRuntimeStatus().killedMobsIncrease();

            task.stop(); // Завершаем задачу 

            BotLogger.info(true, "💀 " + bot.getId() + " убил моба: " + event.getEntity().getType());
        }
    }

    public void unregister() {
            HandlerList.unregisterAll(this);
    }
}
