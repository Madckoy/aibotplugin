package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotIdleTaskConfig;
import com.devone.aibot.core.logic.tasks.configs.BotPatrolTaskConfig;

import org.bukkit.Location;

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

        BotLogger.debug(bot.getId() + " 👀 Start Patroling");

        BotPatrolTask patrolTask = new BotPatrolTask(bot);
        bot.getLifeCycle().getTaskStackManager().pushTask(patrolTask);
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
