package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.BotConstants;

public class BotBreakTaskConfig extends BotTaskConfig{

    public BotBreakTaskConfig() {
        super(BotBreakTaskConfig.class.getSimpleName());
    }

    public BotBreakTaskConfig(String name) {
        super( name);
    }

    public void generateDefaultConfig() {

        config.set("pattern", BotConstants.DEFAULT_PATTERN_BREAK);
        config.set("outer_radius", BotConstants.DEFAULT_SCAN_RANGE);
        config.set("inner_radius", BotConstants.DEFAULT_SCAN_RANGE);
        config.set("offsetX", 0);
        config.set("offsetY", 0);
        config.set("offsetZ", 0);

        super.generateDefaultConfig();
    }

    public String getPattern(){
        return config.getString("pattern", BotConstants.DEFAULT_PATTERN_BREAK);
    }

    public int getOuterRadius(){
        return config.getInt("outer_radius", BotConstants.DEFAULT_SCAN_RANGE);
    }

    public int getInnerRadius(){
        return config.getInt("inner_radius", BotConstants.DEFAULT_SCAN_RANGE);
    }

    public int getOffsetX(){
        return config.getInt("offsetX", 0);
    }

    public int getOffsetY(){
        return config.getInt("offsetY", 0);
    }

    public int getOffsetZ(){
        return config.getInt("offsetZ", 0);
    }


}
