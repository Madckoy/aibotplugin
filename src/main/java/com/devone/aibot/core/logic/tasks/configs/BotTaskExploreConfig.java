package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.BotConstants;

public class BotTaskExploreConfig extends BotTaskConfig{


    public BotTaskExploreConfig() {
        super("BotTaskExplore.yml");
    }

    public BotTaskExploreConfig(String name) {
        super(name);
    }

    public void generateDefaultConfig() {

        config.set("scan_radius", BotConstants.DEFAULT_SCAN_RANGE);

        super.generateDefaultConfig();
    }

    public int getScanRadius() {
        return config.getInt("scan_radius", BotConstants.DEFAULT_SCAN_RANGE);
    }

    
}
