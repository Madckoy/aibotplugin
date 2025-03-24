package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.BotConstants;

public class BotTaskBreakBlockConfig extends BotTaskConfig{

    public BotTaskBreakBlockConfig() {
        super( "BotTaskBreakBlock.yml");
    }

    public BotTaskBreakBlockConfig(String name) {
        super( name);
    }

    public void generateDefaultConfig() {

        config.set("pattern", BotConstants.DEFAULT_PATTERN_BREAK);

        super.generateDefaultConfig();
    }

    public String getPattern(){
        return config.getString("pattern", BotConstants.DEFAULT_PATTERN_BREAK);
    }

}
