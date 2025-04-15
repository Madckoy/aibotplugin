package com.devone.bot.core.logic.task.excavate;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateAnyAroundParams;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;


public class BotExcavateAnyAroundTask extends BotExcavateTask {
    private BotExcavateAnyAroundParams params =  new BotExcavateAnyAroundParams();
    
    public BotExcavateAnyAroundTask(Bot bot) {
        super(bot);

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        setTargetMaterials(null);
        setBreakDirection(AxisDirection.DOWN);

        setPatterName( params.getPatternName());
        setOuterRadius(params.getOuterRadius());
        setInnerRadius(params.getInnerRadius());
        
        setOffsetX(params.getOffsetX());
        setOffsetY(params.getOffsetY());
        setOffsetZ(params.getOffsetZ());



    }

}
