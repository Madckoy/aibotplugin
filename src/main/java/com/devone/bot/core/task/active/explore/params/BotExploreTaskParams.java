package com.devone.bot.core.task.active.explore.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotExploreTaskParams extends BotTaskParams {

    private double scanRadius = BotConstants.DEFAULT_SCAN_RADIUS;

    private boolean pickup = true;

    public BotExploreTaskParams() {
        super();
        setIcon("🌐");
        setObjective("Explore");
    }

    public BotExploreTaskParams(int scanRadius) {
        this();
        this.scanRadius = scanRadius;
    }

    public void setPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public boolean isPickup() {
        return pickup;
    }

    public double getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = scanRadius;
    }

    @Override
    public String toString() {
        return "BotExploreTaskParams{" +
                "scanRadius=" + scanRadius +
                ", pickup=" + pickup +
                '}';
    }
}
