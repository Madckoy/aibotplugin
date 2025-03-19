package com.devone.aibot.core.logic.tasks.configs;

public class BotTaskExploreConfig extends BotAbstractConfig{


    public BotTaskExploreConfig() {
        super("BotTaskExplore.yml");
    }

    public void generateDefaultConfig() {

        config.set("scan_radius", 10);

        super.generateDefaultConfig();
    }

    public int getScanRadius() {
        return config.getInt("scan_radius", 10);
    }

    
}
