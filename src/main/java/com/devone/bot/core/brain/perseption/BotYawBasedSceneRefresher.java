package com.devone.bot.core.brain.perseption;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;


public class BotYawBasedSceneRefresher implements BotYawChangeListener {

    private boolean lock = false;

    @Override
    public void onYawChanged(Bot bot, float newYaw) {
        try{
            BotLogger.debug(bot.getActiveTask().getIcon(), bot.isLogged(), bot.getId() + " YAW CHANGED. New YAW: " + newYaw);
        }catch (Exception ex){}
        
        if (lock) return;

        try {
            lock = true;
            try {
                //read from mem
                int radius = BotConstants.DEFAULT_SCAN_RADIUS;

                Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readMemoryValueTyped(bot, 
                                    BotMemoryPartition.PartitionKey.NAVIGATION.toString(), 
                                    BotMemoryItem.ItemKey.SCAN_RADIUS.toString(), Integer.class);                

                if(scanRadiusFromMem!=null) {
                    radius = scanRadiusFromMem.intValue();
                }
                
                bot.getNavigator().calculate(BotConstants.DEFAULT_NORMAL_SIGHT_FOV, radius, BotConstants.DEFAULT_SCAN_HEIGHT);    
            } catch (Exception e) {
                
            }
        } finally {
            lock = false;
        }
    }
}
