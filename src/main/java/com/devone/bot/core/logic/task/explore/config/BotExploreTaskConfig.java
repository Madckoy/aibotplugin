package com.devone.bot.core.logic.task.explore.config;

import com.devone.bot.core.logic.task.config.BotTaskConfig;
import com.devone.bot.utils.BotConstants;

public class BotExploreTaskConfig extends BotTaskConfig{


    public BotExploreTaskConfig() {
        super(BotExploreTaskConfig.class.getSimpleName());
    }

    public BotExploreTaskConfig(String name) {
        super(name);
    }

    public void generateDefaultConfig() {

        config.set("scan_radius", 2*BotConstants.DEFAULT_SCAN_RANGE);

        super.generateDefaultConfig();
    }

    public int getScanRadius() {
        return config.getInt("scan_radius", BotConstants.DEFAULT_SCAN_RANGE);
    }

    
}
