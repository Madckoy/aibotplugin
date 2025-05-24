package com.devone.bot.core.brain.perseption;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;


public class BotYawBasedSceneRefresher implements BotYawChangeListener {

    private boolean lock = false;

    @Override
    public void onYawChanged(Bot bot, float newYaw) {
        try{
            BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " YAW CHANGED. New YAW: " + newYaw);
        }catch (Exception ex){}
        
        if (lock) return;

        try {
            lock = true;
            try {
                //read from mem
                int radius = BotConstants.DEFAULT_SCAN_RADIUS;
                Integer scanRadius = (Integer) BotMemoryV2Utils.readMemoryValue(bot, "navigation", "scanRadius");               
                if(scanRadius!=null) {
                    radius = scanRadius.intValue();
                }
                bot.getNavigator().calculate(BotConstants.DEFAULT_NORMAL_SIGHT_FOV, radius, BotConstants.DEFAULT_SCAN_HEIGHT);    
            } catch (Exception e) {
                
            }
        } finally {
            lock = false;
        }
    }
}
