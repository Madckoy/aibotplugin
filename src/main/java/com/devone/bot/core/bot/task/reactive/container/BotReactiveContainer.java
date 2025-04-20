package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotReactiveContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveContainer
        extends BotReactiveTaskContainer<BotReactiveContainerParams> {

    public BotReactiveContainer(Bot bot) {
        super(bot, BotReactiveContainerParams.class);
        setObjective("Reactive: Dynamic tasks");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
    }
}
