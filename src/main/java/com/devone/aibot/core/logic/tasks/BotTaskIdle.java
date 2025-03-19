package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotTaskIdleConfig;

import java.util.Set;

import com.devone.aibot.utils.BotStringUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.utils.BotLogger;

public class BotTaskIdle extends BotTask {

    public BotTaskIdle(Bot bot) {
        super(bot, "𖦹");
        this.bot = bot;
        new BotTaskIdleConfig();
    }

    @Override
    public void executeTask() {
  
        double rand = Math.random();

        Set<Material> dirtTypes = Set.of(
                Material.DIRT,
                Material.GRASS_BLOCK,
                Material.PODZOL,
                Material.MYCELIUM,
                Material.COARSE_DIRT,
                Material.ROOTED_DIRT
        );

        int maxDirtToCollect = 128;

        // Check if bot needs to clean up the inventory
        if(!BotInventory.hasFreeInventorySpace(bot, dirtTypes) || BotInventory.hasEnoughBlocks(bot, dirtTypes, maxDirtToCollect)) {

            bot.setAutoPickupEnabled(false);

            BotTaskDropAll drop_task = new BotTaskDropAll(bot, null);
            drop_task.setPaused(true);
            bot.addTaskToQueue(drop_task);
            
            Location drop_off_loc = drop_task.getTargetLocation();
            
            // go to the drop point
            BotTaskMove moveTask = new BotTaskMove(bot);

            moveTask.configure(drop_off_loc);
            bot.addTaskToQueue(moveTask);

            BotLogger.debug("⛏ " + bot.getId() + " Goes to drop off location: " + BotStringUtils.formatLocation(drop_off_loc));

            return;
        }

        if (rand <= 0.8) {
            // 📌 60% шанс начать патрулирование
            BotLogger.debug("👀 " + bot.getId() + " Starts Patrolling");
            BotTaskPatrol patrolTask = new BotTaskPatrol(bot);
            bot.addTaskToQueue(patrolTask);

        } else if (rand <= 0.2) {
            // ⛏ 20% шанс начать добычу
            BotTaskBreakBlock breakTask = new BotTaskBreakBlock(bot);

            if(breakTask.isEnabled) {
                breakTask.configure(dirtTypes, maxDirtToCollect, 10, true); //ломаем все, включая кабины (тестовый режим) и лутаем!!!
                bot.addTaskToQueue(breakTask);

            }

        } else {
            // 💤 20% шанс остаться в IDLE
            BotLogger.debug("💤 " + bot.getId() + " Остаётся в IDLE.");
        }

    }
}
