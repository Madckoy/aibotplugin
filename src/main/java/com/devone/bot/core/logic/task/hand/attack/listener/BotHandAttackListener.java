package com.devone.bot.core.logic.task.hand.attack.listener;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.hand.attack.BotHandAttackTask;
import com.devone.bot.utils.logger.BotLogger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BotHandAttackListener implements Listener {

    private final BotHandAttackTask task;

    public BotHandAttackListener(BotHandAttackTask task) {
        this.task = task;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Bot bot = task.getBot();
        
        if (event.getEntity().getKiller() != null
            && event.getEntity().getKiller().getUniqueId().equals(bot.getNPCEntity().getUniqueId())) {
            
            bot.getRuntimeStatus().killedMobsIncrease(event.getEntity().getName());

            task.stop(); // Завершаем задачу 

            BotLogger.info("💀", true, bot.getId() + " убил моба: " + event.getEntity().getType());
        }
    }

    public void unregister() {
            HandlerList.unregisterAll(this);
    }
}
