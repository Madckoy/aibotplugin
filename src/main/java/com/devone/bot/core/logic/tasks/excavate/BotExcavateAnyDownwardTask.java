package com.devone.bot.core.logic.tasks.excavate;


import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.excavate.config.BotExcavateTaskConfig;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;

public class BotExcavateAnyDownwardTask extends BotExcavateTask {

    public BotExcavateAnyDownwardTask(Bot bot) {
        super(bot);
        
        setName(getName()+" ▼ ");

        setTargetMaterials(null);

        setBreakDirection(AxisDirection.DOWN);

        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotExcavateTaskConfig config = new BotExcavateTaskConfig("BotBreakAnyDownwardTaskConfig");
        this.isLogged = config.isLogged();

        setPatterName(config.getPattern());
        setOuterRadius(config.getOuterRadius());
        setInnerRadius(config.getInnerRadius());
        
        setOffsetX(config.getOffsetX());
        setOffsetY(config.getOffsetY());
        setOffsetZ(config.getOffsetZ());


    }
}
