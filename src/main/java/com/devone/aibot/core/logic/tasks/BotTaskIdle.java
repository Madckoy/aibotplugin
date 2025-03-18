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
        super(bot, "💤");
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

        int maxDirtToCollect = 64;

        // Check if bot needs to clean up the inventory
        if(!BotInventory.hasFreeInventorySpace(bot, dirtTypes) || BotInventory.hasEnoughBlocks(bot, dirtTypes, maxDirtToCollect)) {

            bot.setAutoPickupEnabled(false);

            BotTaskDropAll drop_task = new BotTaskDropAll(bot, null);
            drop_task.setPaused(true);
            bot.getLifeCycle().getTaskStackManager().pushTask(drop_task);
            
            
            // go to the drop point
            BotTaskMove moveTask = new BotTaskMove(bot);
            Location drop_off_loc = Bot.getFallbackLocation();
            moveTask.configure(drop_off_loc);
            bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
            BotLogger.debug("⛏ " + bot.getId() + " Goes to drop off location: " + BotStringUtils.formatLocation(drop_off_loc));

            return;
        }

        if (rand < 0.4) {
            // 📌 40% шанс начать патрулирование
            BotLogger.debug("👀 " + bot.getId() + " Starts Patrolling");
            BotTaskPatrol patrolTask = new BotTaskPatrol(bot);
            bot.getLifeCycle().getTaskStackManager().pushTask(patrolTask);

        } else if (rand < 0.7) {
            // ⛏ 30% шанс начать добычу
            BotLogger.debug("⛏ " + bot.getId() + " Starts Breaking the blocks");
            BotTaskBreakBlock breakTask = new BotTaskBreakBlock(bot);
            
            if(breakTask.isEnabled) {
                breakTask.configure(dirtTypes, maxDirtToCollect, 5, true); //ломаем все, включая кабины (тестовый режим) и лутаем!!!
                bot.getLifeCycle().getTaskStackManager().pushTask(breakTask);
            }

        } else {
            // 💤 30% шанс остаться в IDLE
            BotLogger.debug("💤 " + bot.getId() + " Остаётся в IDLE.");
        }

    }
}
