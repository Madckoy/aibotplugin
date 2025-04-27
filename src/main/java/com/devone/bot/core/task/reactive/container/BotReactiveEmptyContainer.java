package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.reactive.container.params.BotReactiveEmptyContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveEmptyContainer
        extends BotReactiveTaskContainer<BotReactiveEmptyContainerParams> {

    public BotReactiveEmptyContainer(Bot bot) {
        super(bot, BotReactiveEmptyContainerParams.class);
        setIcon("🔀");
        setObjective("Reactive: Empty container");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
    }
}
