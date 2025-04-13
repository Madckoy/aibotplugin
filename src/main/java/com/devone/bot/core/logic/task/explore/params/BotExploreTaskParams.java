package com.devone.bot.core.logic.task.explore.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;

public class BotExploreTaskParams extends BotTaskParams {
    public int scanRadius;
    
    public BotExploreTaskParams(int scanRadius) {
        this.scanRadius = scanRadius;
    }
    public BotExploreTaskParams() {
        this.scanRadius = BotConstants.DEFAULT_SCAN_RANGE;
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
                '}';
    }
}
