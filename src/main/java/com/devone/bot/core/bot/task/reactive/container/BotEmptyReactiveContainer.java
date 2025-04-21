package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotEmptyReactiveContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotEmptyReactiveContainer
        extends BotReactiveTaskContainer<BotEmptyReactiveContainerParams> {

    public BotEmptyReactiveContainer(Bot bot) {
        super(bot, BotEmptyReactiveContainerParams.class);
        setObjective("Reactive: Empty container");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
    }
}
