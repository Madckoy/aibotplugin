package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotTaskHuntConfig;
import com.devone.aibot.core.logic.tasks.configs.BotTaskIdleConfig;
import com.devone.aibot.core.logic.tasks.destruction.BotTaskBreakBlockAnyDownward;

import java.util.Set;

import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.utils.BotLogger;

public class BotTaskIdle extends BotTask {

    public BotTaskIdle(Bot bot) {
        super(bot, "🔀");
        this.bot = bot;
        config = new BotTaskIdleConfig();
        setObjective("Having the rest");
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

        // Проверяем, нужно ли очистить инвентарь
        if (!BotInventory.hasFreeInventorySpace(bot, dirtTypes) || BotInventory.hasEnoughBlocks(bot, dirtTypes, maxToCollect)) {
            bot.setAutoPickupEnabled(false);

            BotTaskDropAll drop_task = new BotTaskDropAll(bot, null);
            drop_task.setPaused(true);
            bot.addTaskToQueue(drop_task);
            
            Location drop_off_loc = bot.getRuntimeStatus().getTargetLocation();
            
            // Перемещение к точке сброса
            BotTaskMove moveTask = new BotTaskMove(bot);
            moveTask.configure(drop_off_loc);
            bot.addTaskToQueue(moveTask);

            BotLogger.debug("📦 " + bot.getId() + " Идёт к точке сброса: " + BotStringUtils.formatLocation(drop_off_loc));
            return;
        }

        // Определяем, день или ночь
        World world = bot.getNPCEntity().getWorld();
        long time = world.getTime();
        boolean isNight = (time >= 13000 && time <= 23000); // Примерно 13000 - закат, 23000 - рассвет

        double huntChance = isNight ? 0.9 : 0.2; // 90% ночью, 20% днем

        if (rand < 0.1) { // 10% шанс сказать что-то про окружающий мир
            BotLogger.debug("🤖 " + bot.getId() + " Комментирует обстановку.");
            bot.addTaskToQueue(new BotTaskTalk(bot, null, BotTaskTalk.TalkType.ENVIRONMENT_COMMENT));
            return;
        }


        if (rand < huntChance) {
            // ⚔️ Охота
            BotLogger.debug("⚔️ " + bot.getId() + " Собирается на охоту! (Вероятность: " + huntChance * 100 + "%)");
           
            BotTaskHuntMobs hunt_task = new BotTaskHuntMobs(bot);
            //BotTaskHuntMobs config  = hunt_task.getConfig();
            Set<EntityType> a_targets = ((BotTaskHuntConfig) hunt_task.getConfig()).getTargetAggressiveMobs();
            Set<EntityType> p_targets = ((BotTaskHuntConfig) hunt_task.getConfig()).getTargetPassiveMobs();
            Set<EntityType> targets = isNight ? a_targets : p_targets;
            
            hunt_task.configure(targets, 20, true);
            bot.addTaskToQueue(hunt_task);
            BotLogger.debug("⚔️ " + bot.getId() + " Начинает охоту на " + (isNight ? "агрессивных мобов" : "животных") + "!");

            return;
        }

        if (rand >= 0.8) {
            // 📌 Начать патрулирование (20% вероятность)
            BotLogger.debug("🌐 " + bot.getId() + " начинает патрулирование.");
            BotTaskExplore patrolTask = new BotTaskExplore(bot);
            bot.addTaskToQueue(patrolTask);
            return;
        }

        /* 
        if (rand < 0.8 && rand >= 0.5) {
            // ⛏ 30% шанс начать добычу земли
            BotTaskBreakBlock breakTask = new BotTaskBreakBlock(bot);
        
            if (breakTask.isEnabled) {
                breakTask.configure(dirtTypes, maxToCollect, 10, true);
                bot.addTaskToQueue(breakTask);
            }
        
            return;
        }
            */

        if (rand < 0.8 && rand >= 0.2) {  
            // ⛏ 30% шанс начать добычу всего подряд вниз
            BotTaskBreakBlockAnyDownward breakAnyTask = new BotTaskBreakBlockAnyDownward(bot);
        
            if (breakAnyTask.isEnabled) {
                breakAnyTask.configure(null, maxToCollect, 10, true);
                bot.addTaskToQueue(breakAnyTask);
            }    
        
            return;
        }

        if (rand < 0.2) {
            // 💤 20% шанс остаться в IDLE
            BotLogger.debug("🔀" + bot.getId() + " остаётся в IDLE.");
            return;
        }
    }
}
