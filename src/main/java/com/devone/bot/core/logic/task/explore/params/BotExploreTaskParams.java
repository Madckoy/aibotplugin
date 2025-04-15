package com.devone.bot.core.logic.task.explore.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;

public class BotExploreTaskParams extends BotTaskParams {
    private int scanRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private String icon = "🌐";
    private String objective = "Explore";
    private boolean pickup = true;
    private boolean pickupAuto = true;
    
    public BotExploreTaskParams(int scanRadius) {
        super(BotExploreTaskParams.class.getSimpleName());
        this.scanRadius = scanRadius;
        setIcon(icon);
        setObjective(objective);
    }
    public BotExploreTaskParams() {
        super(BotExploreTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public boolean shouldPickup() {
        return pickup;

    }

    public boolean shouldPickupAuto() {
        return pickupAuto;

    }

    public int getScanRadius() {
        return scanRadius;
    }
    public void setScanRadius(int scanRadius) {
        this.scanRadius = scanRadius;
    }

    @Override
    public Object setDefaults() {
        config.set("explore.scan.radius",  this.scanRadius);
        config.set("explore.pickup", this.pickup);
        config.set("explore.pickup.auto", this.pickupAuto);
        super.setDefaults();
        return this;
    }

    @Override
    public String toString() {
        return "BotExploreTaskParams{" +
                "scanRadius=" + scanRadius +
                '}';
    }
}
