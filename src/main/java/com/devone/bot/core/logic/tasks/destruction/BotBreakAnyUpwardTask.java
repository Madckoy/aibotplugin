package com.devone.bot.core.logic.tasks.destruction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.bot.utils.BotAxisDirection.AxisDirection;


public class BotBreakAnyUpwardTask extends BotBreakTask {

    public BotBreakAnyUpwardTask(Bot bot) {
        super(bot);

        setName(getName()+" ▲ ");

        setTargetMaterials(null);

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
}
