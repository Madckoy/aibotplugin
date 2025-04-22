package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.passive.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.passive.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotTeleportToLocationReactiveContainerParams;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTeleportToLocationReactiveContainer
        extends BotReactiveTaskContainer<BotTeleportToLocationReactiveContainerParams> {

    private final BotLocation location;

    public BotTeleportToLocationReactiveContainer(Bot bot, BotLocation location) {
        super(bot, BotTeleportToLocationReactiveContainerParams.class);
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
        add(tp);
    }
}
