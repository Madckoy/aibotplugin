package com.devone.aibot.core.logic.tasks.configs;

public class BotLocationConfig extends BotTaskConfig{

    public BotLocationConfig(String f_name) {
        super(BotLocationConfig.class.getSimpleName());
    }

    public void generateDefaultConfig() {

        config.set("X", 0);
        config.set("Y", 0);
        config.set("Z", 0);

        super.generateDefaultConfig();
    }

    
    public int getX(){
        return getConfig().getInt("X",0);
    }

    public int getY(){
        return getConfig().getInt("Y", 0);
    }
    
    public int getZ(){
        return getConfig().getInt("Z", 0);
    }

}
