package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.BotConstants;

public class BotBreakTaskConfig extends BotTaskConfig{

    public BotBreakTaskConfig() {
        super( "BotBreakTaskConfig.yml");
    }

    public BotBreakTaskConfig(String name) {
        super( name);
    }

    public void generateDefaultConfig() {

        config.set("pattern", BotConstants.DEFAULT_PATTERN_BREAK);
        config.set("break_radius", BotConstants.DEFAULT_SCAN_RANGE);

        super.generateDefaultConfig();
    }

    public String getPattern(){
        return config.getString("pattern", BotConstants.DEFAULT_PATTERN_BREAK);
    }

    public int getBreakRadius(){
        return config.getInt("break_radius", BotConstants.DEFAULT_SCAN_RANGE);
    }

}
