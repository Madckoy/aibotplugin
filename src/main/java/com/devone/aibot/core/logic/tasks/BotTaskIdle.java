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
        super(bot, "🌀");
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

        int maxToCollect = 128;

        // Check if bot needs to clean up the inventory
        if(!BotInventory.hasFreeInventorySpace(bot, dirtTypes) || BotInventory.hasEnoughBlocks(bot, dirtTypes, maxToCollect)) {

            bot.setAutoPickupEnabled(false);

            BotTaskDropAll drop_task = new BotTaskDropAll(bot, null);
            drop_task.setPaused(true);
            bot.addTaskToQueue(drop_task);
            
            Location drop_off_loc = drop_task.getTargetLocation();
            
            // go to the drop point
            BotTaskMove moveTask = new BotTaskMove(bot);

            moveTask.configure(drop_off_loc);
            bot.addTaskToQueue(moveTask);

            BotLogger.debug("📦 " + bot.getId() + " Goes to drop off location: " + BotStringUtils.formatLocation(drop_off_loc));

            return;
        }

        if (rand >= 0.8) {
            // 📌 начать exploration (20% вероятность)
            BotLogger.debug("🌐 " + bot.getId() + " Starts Patrolling");
        
            BotTaskExplore patrolTask = new BotTaskExplore(bot);
            bot.addTaskToQueue(patrolTask);
        
            return;
        }
        
        if (rand < 0.8 && rand >= 0.5) {
           // ⛏ 30% шанс начать добычу земли
           BotTaskBreakBlock breakTask = new BotTaskBreakBlock(bot);
        
           if (breakTask.isEnabled) {
               breakTask.configure(dirtTypes, maxToCollect, 10, true); // ломаем землю и лутаем!!!
               bot.addTaskToQueue(breakTask);
           }
        
           return;
        }
        
        if (rand < 0.5 && rand >= 0.2) {  
             // ⛏ 30% шанс начать добычу всего подряд (раньше эта ветка не выполнялась)
            BotTaskBreakBlockAny breakAnyTask = new BotTaskBreakBlockAny(bot);
        
            if (breakAnyTask.isEnabled) {
                breakAnyTask.configure(null, maxToCollect, 10, true); // ломаем все и лутаем!!!
                bot.addTaskToQueue(breakAnyTask);
            }    
        
            return;
        }
        
        if (rand < 0.2) {
            // 💤 20% шанс остаться в IDLE
            BotLogger.debug("💤 " + bot.getId() + " Остаётся в IDLE.");
        
            return;
        }
    }
}
