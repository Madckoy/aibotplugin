package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotIdleTaskConfig;
import com.devone.aibot.core.logic.tasks.configs.BotPatrolTaskConfig;

import java.util.Set;

import com.devone.aibot.utils.BotStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.commands.BotMove;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.utils.BotLogger;

public class BotIdleTask implements BotTask {
    private final Bot bot;
    private boolean isPaused = false;
    private final String name = "IDLE";
    private final BotIdleTaskConfig config;
    private final BotPatrolTaskConfig patrolConfig;
    private long startTime = System.currentTimeMillis();

    public BotIdleTask(Bot bot) {
        this.bot = bot;
        this.config = new BotIdleTaskConfig();
        this.patrolConfig = new BotPatrolTaskConfig();
    }

    @Override
    public void configure(Object... params) {
        startTime = System.currentTimeMillis();
        // Читаем конфиг, если нужно что-то обновить динамически
    }

    @Override
    public void update() {
        BotLogger.info("update(): "+bot.getId() + " Running task: " + name);

        if (isPaused) return;
    
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
            BotMoveTask moveTask = new BotMoveTask(bot);
            Location drop_off_loc = new Location(Bukkit.getWorld("world"), 0.0, -60.0, 0.0);
            moveTask.configure(drop_off_loc);
            bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
            BotLogger.info("⛏ " + bot.getId() + " Goes to drop off location: " + BotStringUtils.formatLocation(drop_off_loc));
            return;
        }

        
        if (rand < 0.4) {
            // 📌 40% шанс начать патрулирование
            BotLogger.info("👀 " + bot.getId() + " Starts Patrolling");
            BotPatrolTask patrolTask = new BotPatrolTask(bot);
            bot.getLifeCycle().getTaskStackManager().pushTask(patrolTask);

        } else if (rand < 0.7) {
            // ⛏ 30% шанс начать добычу
            BotLogger.info("⛏ " + bot.getId() + " Starts Breaking the blocks");
            BotBreakBlockTask breakTask = new BotBreakBlockTask(bot);
            breakTask.configure(dirtTypes, maxDirtToCollect, 5, true); //ломаем все, включая кабины (тестовый режим) и лутаем!!!
            bot.getLifeCycle().getTaskStackManager().pushTask(breakTask);

        } else {
            // 💤 30% шанс остаться в IDLE
            BotLogger.info("⭕ " + bot.getId() + " Остаётся в IDLE.");
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (isPaused) {
            BotLogger.info("꩜ " + bot.getId() + " ꩜ Pausing...");
        } else {
            BotLogger.info("▶️ " + bot.getId() + " ꩜ Resuming...");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getTargetLocation() {
        return bot.getNPCCurrentLocation();
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}
