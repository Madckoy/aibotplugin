package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotCfgTaskPatrol;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtils;

import org.bukkit.Location;

public class BotTaskPatrol implements BotTask {
    private final Bot bot;
    private final BotCfgTaskPatrol config;
    private boolean isPaused = false;
    private final String name = "PATROL";
    private Location patrolTarget;
    private long startTime = System.currentTimeMillis();
    private  int patrolRadius = 10;

    public BotTaskPatrol(Bot bot) {
        this.bot = bot;
        this.config = new BotCfgTaskPatrol();

        this.patrolRadius = config.getPatrolRadius();
    }

    @Override
    public void configure(Object... params) {
        startTime = System.currentTimeMillis();
        // Можно использовать параметры для динамической настройки патрулирования
    }

    @Override
    public void update() {
        BotLogger.info("✨ "+bot.getId() + " Running task: " + name);

        if (isPaused) return;

        if (shouldExitPatrol()) {
            BotLogger.info("👀 "+bot.getId() + " Has finished patrolling.");
            return;
        }

        patrolRadius = config.getPatrolRadius();
        BotLogger.info("👀 "+bot.getId() + " Patrolling with radius:  " + patrolRadius);

        patrolTarget = BotNavigationUtils.getRandomWalkLocation(bot.getNPCCurrentLocation(), -patrolRadius, patrolRadius);

        patrolTarget = BotNavigationUtils.findNearestNavigableLocation(bot.getNPCCurrentLocation(), patrolTarget, patrolRadius);

        BotTaskMove moveTask = new BotTaskMove(bot);
        moveTask.configure(patrolTarget);

        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
    }

    private boolean shouldExitPatrol() {
        
        if ( patrolTarget == null ) return false;
        
        if(BotNavigationUtils.hasReachedTarget(bot, patrolTarget, 2.0)) {
            patrolTarget = BotNavigationUtils.getRandomWalkLocation(bot.getNPCCurrentLocation(), -patrolRadius, patrolRadius);
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
        if (isPaused) {
            BotLogger.info("ℹ️ " + bot.getId() + " ꩜ Pausing...");
        } else {
            BotLogger.info("ℹ️ " + bot.getId() + " ꩜ Resuming...");
        }
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