package com.devone.bot.core.task.reactive.container;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.reactive.container.params.BotReactiveEmptyContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveEmptyContainer
        extends BotReactiveContainer<BotReactiveEmptyContainerParams> {

    public BotReactiveEmptyContainer(Bot bot) {
        super(bot, BotReactiveEmptyContainerParams.class);
        setIcon("🔣");
        setObjective("Reactive: Empty container");
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
                List<BotTask<?>> subtasks = new ArrayList<>();
        return subtasks;
    }
}
