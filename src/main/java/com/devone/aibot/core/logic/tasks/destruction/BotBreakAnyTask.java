package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;

public class BotBreakAnyTask extends BotBreakTask {
    
    public BotBreakAnyTask(Bot bot) {
        super(bot);

        //setName("🪨👁🧑‍🔧");

        setTargetMaterials(null);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
        
        setScanMode(ScanMode.FULL);

        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyTask.yml");
        logging = config.isLogging();
        patternName = config.getPattern();
        // Передай в configure строку с именем yaml-файла
        //super.configure(null, 0, getBreakRadius(), true, false, null, config.getPattern());
    }
 
    @Override
    public void executeTask() {
        super.executeTask();
    }

}
