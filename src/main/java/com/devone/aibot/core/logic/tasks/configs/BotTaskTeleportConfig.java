package com.devone.aibot.core.logic.tasks.configs;

public class BotTaskTeleportConfig extends BotAbstractLocationConfig {


    public BotTaskTeleportConfig() {
        super("BotTaskTeleport.yml");
    }

    public void generateDefaultConfig() {

        config.set("X", 10);
        config.set("Y", 10);
        config.set("Z", 10);

        super.generateDefaultConfig();
    }

}
