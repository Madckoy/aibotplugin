package com.devone.bot.core.logic.tasks.destruction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.bot.utils.BotAxisDirection.AxisDirection;


public class BotBreakAnyTask extends BotBreakTask {
    
    public BotBreakAnyTask(Bot bot) {
        super(bot);

        setName(getName()+" ✴ ");

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        setTargetMaterials(null);
        setBreakDirection(AxisDirection.DOWN);


        BotBreakTaskConfig config = new BotBreakTaskConfig("BotBreakAnyTaskConfig");
        this.isLogged = config.isLogged();

        setPatterName(config.getPattern());
        setOuterRadius(config.getOuterRadius());
        setInnerRadius(config.getInnerRadius());
        
        setOffsetX(config.getOffsetX());
        setOffsetY(config.getOffsetY());
        setOffsetZ(config.getOffsetZ());



    }

}
