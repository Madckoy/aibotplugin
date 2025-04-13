package com.devone.bot.core.logic.task.excavate;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.excavate.config.BotExcavateTaskConfig;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;


public class BotExcavateAnyTask extends BotExcavateTask {
    
    public BotExcavateAnyTask(Bot bot) {
        super(bot);

        setName(getName()+" ✴ ");

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        setTargetMaterials(null);
        setBreakDirection(AxisDirection.DOWN);


        BotExcavateTaskConfig config = new BotExcavateTaskConfig("BotBreakAnyTaskConfig");
        this.isLogged = config.isLogged();

        setPatterName(config.getPattern());
        setOuterRadius(config.getOuterRadius());
        setInnerRadius(config.getInnerRadius());
        
        setOffsetX(config.getOffsetX());
        setOffsetY(config.getOffsetY());
        setOffsetZ(config.getOffsetZ());



    }

}
