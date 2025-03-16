package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotIdleTaskConfig;
import com.devone.aibot.core.logic.tasks.configs.BotPatrolTaskConfig;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.utils.BotLogger;

public class BotIdleTask implements BotTask {
    private final Bot bot;
    private boolean isPaused = false;
    private String name = "IDLE";
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
        // Читаем конфиг, если нужно что-то обновить динамически
    }

    @Override
    public void update() {
        BotLogger.debug(bot.getId() + " Running task: " + name);

        if (isPaused) return;
    
        double rand = Math.random();
        
        if (rand < 0.4) {
            // 📌 40% шанс начать патрулирование
            BotLogger.debug(bot.getId() + " 👀 Start Patroling");
            BotPatrolTask patrolTask = new BotPatrolTask(bot);
            bot.getLifeCycle().getTaskStackManager().pushTask(patrolTask);
        } else if (rand < 0.7) {
            // ⛏ 30% шанс начать добычу
            BotLogger.debug(bot.getId() + " ⛏ Start Mining");

            Set<Material> dirtTypes = Set.of(
                    Material.DIRT, 
                    Material.GRASS_BLOCK, 
                    Material.PODZOL, 
                    Material.MYCELIUM, 
                    Material.COARSE_DIRT, 
                    Material.ROOTED_DIRT
            );

            BotBreakBlockTask breakTask = new BotBreakBlockTask(bot);

            BotInventory.dropAllItems(bot);

            breakTask.configure(dirtTypes, 256, 5, true); //ломаем все, включая кабины (тестовый режим) и лутаем!!!

            bot.getLifeCycle().getTaskStackManager().pushTask(breakTask);
        } else {
            // 💤 30% шанс остаться в IDLE
            BotLogger.debug(bot.getId() + " 🌙 Остаётся в IDLE.");
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        BotLogger.debug(bot.getId() + (paused ? " ꩜ Waiting" : " ▶️ Resuming"));
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
