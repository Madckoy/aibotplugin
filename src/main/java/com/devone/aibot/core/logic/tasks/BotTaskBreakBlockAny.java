package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;

public class BotTaskBreakBlockAny extends BotTaskBreakBlock {
    
    public BotTaskBreakBlockAny(Bot bot) {
        super(bot);

        setName("⛏");

        setTargetMaterials(null);

        setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

    }
 
    @Override
    public void executeTask() {
        super.executeTask();
    }

}
