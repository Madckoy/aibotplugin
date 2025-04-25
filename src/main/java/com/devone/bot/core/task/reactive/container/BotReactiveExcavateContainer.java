package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;

import com.devone.bot.core.task.reactive.container.params.BotReactiveExcavateContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveExcavateContainer extends BotReactiveTaskContainer<BotReactiveExcavateContainerParams> {

    public BotReactiveExcavateContainer(Bot bot) {

        super(bot, BotReactiveExcavateContainerParams.class);

        setObjective("Reactive: Bot Move and Excavate Tasks");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
        BotExcavateTaskParams excvParams = new BotExcavateTaskParams();
        BotExcavateTask excvTask = new BotExcavateTask(bot);
        excvTask.setParams(excvParams);
        add(excvTask);
    }

}
