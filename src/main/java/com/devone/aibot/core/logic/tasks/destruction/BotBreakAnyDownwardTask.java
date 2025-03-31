package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;

public class BotBreakAnyDownwardTask extends BotBreakTask {

    public BotBreakAnyDownwardTask(Bot bot) {
        super(bot);
        
        setName(getName()+"(▼)");

        setTargetMaterials(null);

        setScanMode(ScanMode.DOWNWARD);
        
        setDirection(AxisDirection.DOWN);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyDownwardTask.yml");
        logging = config.isLogging();
        patternName = config.getPattern();
        // Передай в configure строку с именем yaml-файла
        //super.configure(null, 0, getBreakRadius(), true, false, null, config.getPattern());
    }
}
