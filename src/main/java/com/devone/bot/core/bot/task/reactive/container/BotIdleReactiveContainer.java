package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.idle.BotIdleTask;
import com.devone.bot.core.bot.task.active.idle.params.BotIdleTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotIdleReactiveContainerParams;

import com.devone.bot.core.utils.logger.BotLogger;

public class BotIdleReactiveContainer extends BotReactiveTaskContainer<BotIdleReactiveContainerParams> {

    public BotIdleReactiveContainer(Bot bot) {

        super(bot, BotIdleReactiveContainerParams.class);

        setObjective("Reactive: BotIdleTask");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotIdleTaskParams tpParams = new BotIdleTaskParams();
        BotIdleTask tpTask = new BotIdleTask(bot);
        tpTask.setParams(tpParams);

        bot.pushReactiveTask(tpTask);
    }

}
