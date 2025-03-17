package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskPatrolConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigation;
import org.bukkit.Location;

public class BotTaskPatrol extends BotTask {
  
    private Location patrolTarget;
    private int patrolRadius = 10;
    private BotTaskPatrolConfig config;

    public BotTaskPatrol(Bot bot) {

        super(bot, "PATROL");

        this.config = new BotTaskPatrolConfig();

        this.patrolRadius = config.getPatrolRadius();
    }

    public void executeTask() {

        if (isPaused) return;

        if (shouldExitPatrol()) {
            BotLogger.info("👀 "+bot.getId() + " Has finished patrolling.");
            return;
        }

        BotLogger.info("👀 "+bot.getId() + " Patrolling with radius:  " + patrolRadius);

        patrolTarget = BotNavigation.getRandomPatrolPoint(bot, patrolRadius);

 
        BotNavigation.navigateTo(bot, patrolTarget, 10);

    }

    private boolean shouldExitPatrol() {
        
        if ( patrolTarget == null ) return false;
        
        if(BotNavigation.hasReachedTarget(bot, patrolTarget, 4.0)) {
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
    public Location getTargetLocation() {
        return patrolTarget != null ? patrolTarget : bot.getNPCCurrentLocation();
    }

}
