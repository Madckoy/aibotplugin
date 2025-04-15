package com.devone.bot.core.logic.task.excavate;


import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateAnyDownwardTaskParams;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;

public class BotExcavateAnyDownwardTask extends BotExcavateTask {

    private BotExcavateAnyDownwardTaskParams params = new BotExcavateAnyDownwardTaskParams();

    public BotExcavateAnyDownwardTask(Bot bot) {
        super(bot);
        
        setIcon(params.getIcon());

        setTargetMaterials(null);

        setBreakDirection(AxisDirection.DOWN);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        setPatterName( params.getPatternName());
        setOuterRadius(params.getOuterRadius());
        setInnerRadius(params.getInnerRadius());
        
        setOffsetX(params.getOffsetX());
        setOffsetY(params.getOffsetY());
        setOffsetZ(params.getOffsetZ());


    }
}
