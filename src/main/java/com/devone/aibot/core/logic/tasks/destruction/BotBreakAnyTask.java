package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
import com.devone.aibot.utils.BotConstants;


public class BotBreakAnyTask extends BotBreakTask {
    
    public BotBreakAnyTask(Bot bot) {
        super(bot);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        setTargetMaterials(null);
        setScanMode(ScanMode.FULL);
        setBreakDirection(AxisDirection.DOWN);
        setOffsetX(0);
        setOffsetY(-1*BotConstants.DEFAULT_SCAN_RANGE);
        setOffsetZ(0);

        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyTask.yml");
        logging = config.isLogging();

        setPatterName(config.getPattern());


    }
 
    @Override
    public void executeTask() {
        super.executeTask();
    }

}
