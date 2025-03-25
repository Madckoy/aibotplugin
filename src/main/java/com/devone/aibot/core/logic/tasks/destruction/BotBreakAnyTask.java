package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakBlockTaskConfig;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotBreakAnyTask extends BotBreakTask {
    
    public BotBreakAnyTask(Bot bot) {
        super(bot);

        //setName("🪨👁🧑‍🔧");

        setTargetMaterials(null);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
        
        setScanMode(ScanMode.FULL);

        BotBreakBlockTaskConfig config = new BotBreakBlockTaskConfig("BotBreakAnyTask.yml");
        // Передай в configure строку с именем yaml-файла
        this.configure(null, 0, getBreakRadius(), true, false, null, config.getPattern());
    }
 
    @Override
    public void executeTask() {
        super.executeTask();
    }

}
