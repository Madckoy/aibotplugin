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


        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyUpwardTaskConfig");
        this.isLogged = config.isLogged();

        setPatterName(config.getPattern());
        setOuterRadius(config.getOuterRadius());
        setInnerRadius(config.getInnerRadius());
        
        setOffsetX(config.getOffsetX());
        setOffsetY(config.getOffsetY());
        setOffsetZ(config.getOffsetZ());

    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
