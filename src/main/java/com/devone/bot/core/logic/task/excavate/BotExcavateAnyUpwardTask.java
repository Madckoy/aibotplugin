package com.devone.bot.core.logic.task.excavate;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateAnyUpwardTaskParams;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;


public class BotExcavateAnyUpwardTask extends BotExcavateTask {

    private BotExcavateAnyUpwardTaskParams params = new BotExcavateAnyUpwardTaskParams();

    public BotExcavateAnyUpwardTask(Bot bot) {
        super(bot);

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        setTargetMaterials(null);

        setBreakDirection(AxisDirection.UP);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        setPatterName(params.getPatternName());
        setOuterRadius(params.getOuterRadius());
        setInnerRadius(params.getInnerRadius());
        
        setOffsetX(params.getOffsetX());
        setOffsetY(params.getOffsetY());
        setOffsetZ(params.getOffsetZ());

    }
}
