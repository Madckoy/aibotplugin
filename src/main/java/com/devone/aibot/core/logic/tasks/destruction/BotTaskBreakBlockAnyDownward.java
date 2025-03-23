package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakDefaultPattern;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakSpiral3DPatternDown;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAnyDownward extends BotTaskBreakBlock {
    
    public BotTaskBreakBlockAnyDownward(Bot bot) {
        
        super(bot);

        setName("⛏");

        setTargetMaterials(null);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
         
        setScanMode(ScanMode.DOWNWARD);
        
        setBreakPattern(new BotBreakDefaultPattern());

    }
 
}
