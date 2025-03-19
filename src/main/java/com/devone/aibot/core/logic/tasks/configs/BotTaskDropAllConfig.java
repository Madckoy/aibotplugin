package com.devone.aibot.core.logic.tasks.configs;

public class BotTaskDropAllConfig extends BotConfig{


    public BotTaskDropAllConfig() {
        super("BotTaskAdropAll.yml");
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
