package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakBlockTaskConfig;
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

        BotBreakBlockTaskConfig config = new BotBreakBlockTaskConfig("BotBreakAnyDownwardTask.yml");
        // Передай в configure строку с именем yaml-файла
        this.configure(null, 0, getBreakRadius(), true, false, null, config.getPattern());
    }
}
