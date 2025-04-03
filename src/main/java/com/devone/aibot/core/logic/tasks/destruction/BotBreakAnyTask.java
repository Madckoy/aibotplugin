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


        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyTask.yml");
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
