package com.devone.bot.core.brain.perseption;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;


public class YawBasedSceneRefresher implements BotYawChangeListener {

    private boolean lock = false;

    @Override
    public void onYawChanged(Bot bot, float newYaw) {
        try{
            BotLogger.debug(bot.getActiveTask().getIcon(), true, bot.getId() + " YAW CHANGED. New YAW: " + newYaw);
        }catch (Exception ex){}
        
        if (lock) return;

        try {
            lock = true;
            bot.getNavigator().calculate(bot.getBrain().getSceneData(), BotConstants.DEFAULT_NORMAL_SIGHT_FOV);
        } finally {
            lock = false;
        }
    }
}
