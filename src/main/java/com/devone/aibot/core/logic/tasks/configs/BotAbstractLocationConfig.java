package com.devone.aibot.core.logic.tasks.configs;

public class BotAbstractLocationConfig extends BotAbstractConfig{


    public BotAbstractLocationConfig(String f_name) {
        super(f_name);
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
