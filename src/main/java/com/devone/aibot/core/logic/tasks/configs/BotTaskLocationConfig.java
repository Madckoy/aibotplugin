package com.devone.aibot.core.logic.tasks.configs;

public class BotTaskLocationConfig extends BotTaskConfig{


    public BotTaskLocationConfig(String f_name) {
        super(f_name);
    }

    public void generateDefaultConfig() {

            config.set("X", 10);
            config.set("Y", 10);
            config.set("Z", 10);

        super.generateDefaultConfig();
    }

    
    public int getX(){
        return getConfig().getInt("X",10);
    }

    public int getY(){
        return getConfig().getInt("Y", 10);
    }
    
    public int getZ(){
        return getConfig().getInt("Z", 10);
    }

}
