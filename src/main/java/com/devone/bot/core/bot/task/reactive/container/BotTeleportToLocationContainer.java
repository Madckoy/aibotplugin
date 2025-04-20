package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;

import com.devone.bot.core.bot.task.reactive.container.params.BotTeleportToPlayerContainerParams;
import com.devone.bot.core.utils.blocks.BotLocation;

public class BotTeleportToLocationContainer extends BotReactiveTaskContainer<BotTeleportToPlayerContainerParams> {

    private final BotLocation location;

    public BotTeleportToLocationContainer(Bot bot, BotLocation location) {
        super(bot, BotTeleportToPlayerContainerParams.class);
        this.location = location;
        setIcon("📍");
        setObjective("Контейнер для BotTeleportTask");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotTeleportTask tp = new BotTeleportTask(bot, null);
        BotTeleportTaskParams params = new BotTeleportTaskParams();
        params.setLocation(location);
        tp.setParams(params);
        bot.reactiveTaskStart(tp);
    }
}
