package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotIdleTaskConfig;
import com.devone.aibot.core.logic.tasks.configs.BotPatrolTaskConfig;

import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.core.Bot;
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
            BotBreakBlockTask mineTask = new BotBreakBlockTask(bot);
            mineTask.configure(Material.DIRT, 64, 4);
            bot.getLifeCycle().getTaskStackManager().pushTask(mineTask);
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
