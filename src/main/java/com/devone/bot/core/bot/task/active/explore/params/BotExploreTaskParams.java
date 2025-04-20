package com.devone.bot.core.bot.task.active.explore.params;

import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotExploreTaskParams extends BotTaskParams {

    private int scanRadius = BotConstants.DEFAULT_SCAN_RANGE;

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

    public int getScanRadius() {
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
