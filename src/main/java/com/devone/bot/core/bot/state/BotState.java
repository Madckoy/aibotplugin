package com.devone.bot.core.bot.state;

import com.devone.bot.core.bot.Bot;

public class BotState {

    // runtime states

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
    }

    public boolean isLowHealth(double threshold) {
        return this.health < threshold;
    }
}
