package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;

import com.devone.bot.core.bot.task.reactive.container.params.BotTeleportToPlayerReactiveContainerParams;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTeleportToLocationReactiveContainer
        extends BotReactiveTaskContainer<BotTeleportToPlayerReactiveContainerParams> {

    private final BotLocation location;

    public BotTeleportToLocationReactiveContainer(Bot bot, BotLocation location) {
        super(bot, BotTeleportToPlayerReactiveContainerParams.class);
        this.location = location;
        setObjective("Reactive: BotTeleportTask");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotTeleportTask tp = new BotTeleportTask(bot, null);
        BotTeleportTaskParams params = new BotTeleportTaskParams();
        params.setLocation(location);
        tp.setParams(params);
        bot.pushReactiveTask(tp);
    }
}
