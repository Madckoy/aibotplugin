package com.devone.bot.core.bot.state;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotState {

    // runtime states
    private boolean stuck;
    private int stuckCount;
    private transient Bot owner;
    // auto pick ip items

    public Bot getOwner() {
        return owner;
    }

    private double health = 100;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public BotState(Bot bot) {
        this.owner = bot;
        this.stuck = false;
        this.stuckCount = 0;
    }

    // Флаг застревания
    public boolean isStuck() {
        return stuck;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
        BotLogger.debug(owner.getActiveTask().getIcon(), true, owner.getId() + " ❓ BotState: set Stuck="+stuck);
        incrementStuckCount();
    }

    // Счётчик застревания
    public int getStuckCount() {
        return stuckCount;
    }

    public void incrementStuckCount() {
        this.stuckCount++;
    }

    public void resetStuckCount() {
        this.stuckCount = 0;
    }

    public boolean isLowHealth(double threshold) {
        return this.health < threshold;
    }
}
