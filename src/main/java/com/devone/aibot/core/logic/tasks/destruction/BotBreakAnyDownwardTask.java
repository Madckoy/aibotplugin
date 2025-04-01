package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;

public class BotBreakAnyDownwardTask extends BotBreakTask {

    public BotBreakAnyDownwardTask(Bot bot) {
        super(bot);
        
        setName(getName()+"(▼)");

        setTargetMaterials(null);

        setScanMode(ScanMode.DOWNWARD);
        setBreakDirection(AxisDirection.DOWN);

        setOffsetX(0);
        setOffsetY(-1*BotConstants.DEFAULT_SCAN_RANGE);
        setOffsetZ(0);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyDownwardTask.yml");
        logging = config.isLogging();
        patternName = config.getPattern();

    }
}
