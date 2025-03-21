package com.devone.aibot.core.logic.tasks.configs;

public class BotTaskExploreConfig extends BotTaskConfig{


    public BotTaskExploreConfig() {
        super("BotTaskExplore.yml");
    }

    public BotTaskExploreConfig(String name) {
        super(name);
    }

    public void generateDefaultConfig() {

        config.set("scan_radius", 10);

        super.generateDefaultConfig();
    }

    public int getScanRadius() {
        return config.getInt("scan_radius", 10);
    }

    
}
