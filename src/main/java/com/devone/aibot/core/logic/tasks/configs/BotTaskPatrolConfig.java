package com.devone.aibot.core.logic.tasks.configs;

public class BotTaskPatrolConfig extends BotConfig{


    public BotTaskPatrolConfig() {
        super("BotTaskPatrol.yml");
    }

    public void generateDefaultConfig() {

        config.set("scan_radius", 10);

        super.generateDefaultConfig();
    }

    public int getScanRadius() {
        return config.getInt("scan_radius", 10);
    }

    
}
