package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAny extends BotTaskBreakBlock {
    
    public BotTaskBreakBlockAny(Bot bot) {
        super(bot);

        setName("⛏");

        setTargetMaterials(null);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
        
        setScanMode(ScanMode.FULL);
    }
 
    @Override
    public void executeTask() {
        super.executeTask();
    }

}
