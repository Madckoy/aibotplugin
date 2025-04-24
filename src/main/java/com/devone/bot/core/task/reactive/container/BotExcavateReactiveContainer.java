package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotExcavateReactiveContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotExcavateReactiveContainer extends BotReactiveTaskContainer<BotExcavateReactiveContainerParams> {

    public BotExcavateReactiveContainer(Bot bot) {

        super(bot, BotExcavateReactiveContainerParams.class);

        setObjective("Reactive: BotExcavateTask");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotExcavateTaskParams excvParams = new BotExcavateTaskParams();
        BotExcavateTask tpTask = new BotExcavateTask(bot);
        tpTask.setParams(excvParams);

        add(tpTask);
    }

}
