package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskBreakBlockConfig;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAny extends BotTaskBreakBlock {
    
    public BotTaskBreakBlockAny(Bot bot) {
        super(bot);

        //setName("🪨❓🧑‍🔧");

        setTargetMaterials(null);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
        
        setScanMode(ScanMode.FULL);

        BotTaskBreakBlockConfig config = new BotTaskBreakBlockConfig("BotTaskBreakBlockAny.yml");
        // Передай в configure строку с именем yaml-файла
        this.configure(null, 0, getSearchRadius(), true, false, null, config.getPattern());
    }
 
    @Override
    public void executeTask() {
        super.executeTask();
    }

}
