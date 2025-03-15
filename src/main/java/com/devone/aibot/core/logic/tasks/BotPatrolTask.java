package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotPatrolTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtil;
import com.devone.aibot.utils.BotUtils;

import org.bukkit.Location;

import java.util.Random;

public class BotPatrolTask implements BotTask {
    private final Bot bot;
    private final Random random = new Random();
    private final BotPatrolTaskConfig config;
    private boolean isPaused = false;
    private String name = "PATROL";
    private Location patrolTarget;
    private long startTime = System.currentTimeMillis();
    private  int patrolRadius = 10;

    public BotPatrolTask(Bot bot) {
        this.bot = bot;
        this.config = new BotPatrolTaskConfig();

        this.patrolRadius = config.getPatrolRadius();
    }

    @Override
    public void configure(Object... params) {
        // Можно использовать параметры для динамической настройки патрулирования
    }

    @Override
    public void update() {
        if (isPaused) return;

        if (shouldExitPatrol()) {
            BotLogger.debug(bot.getId() +" 👀 Finishing patrolling...");
            return;
        }

        patrolRadius = config.getPatrolRadius();
        BotLogger.debug(bot.getId() + " 👀 Patrolling with radius:  " + patrolRadius);

        patrolTarget = BotNavigationUtil.getRandomWalkLocation(bot.getNPCCurrentLocation(), -patrolRadius, patrolRadius);

        BotMoveTask moveTask = new BotMoveTask(bot);
        moveTask.configure(patrolTarget);

        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
    }

    private boolean shouldExitPatrol() {
        
        if ( patrolTarget == null ) return false;
        
        if(BotUtils.hasReachedTarget(bot.getNPCCurrentLocation(), patrolTarget, 2.0)) {
            patrolTarget = BotNavigationUtil.getRandomWalkLocation(bot.getNPCCurrentLocation(), -patrolRadius, patrolRadius);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDone() {
        return shouldExitPatrol();
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
        return patrolTarget != null ? patrolTarget : bot.getNPCCurrentLocation();
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}