package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
import com.devone.aibot.utils.BotConstants;


public class BotBreakAnyUpwardTask extends BotBreakTask {

    public BotBreakAnyUpwardTask(Bot bot) {
        super(bot);

        setName(getName()+"(▲)");

        setTargetMaterials(null);

        setScanMode(ScanMode.UPWARD);
        setBreakDirection(AxisDirection.UP);
        setOffsetX(0);
        setOffsetY(BotConstants.DEFAULT_SCAN_RANGE);
        setOffsetZ(0);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyUpwardTask.yml");
        logging = config.isLogging();

        setPatterName(config.getPattern());
        setPatterName(config.getPattern());
        setBreakRadius(config.getBreakRadius());

    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
