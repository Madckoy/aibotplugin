package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotLowHealthReactiveContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotLowHealthReactiveContainer extends BotReactiveTaskContainer<BotLowHealthReactiveContainerParams> {

    public BotLowHealthReactiveContainer(Bot bot) {
        super(bot, BotLowHealthReactiveContainerParams.class);
        setObjective("Reactive: BotTeleportTask if low HP");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
        tpParams.setLocation(BotWorldHelper.getWorldSpawnLocation());

        BotTeleportTask tpTask = new BotTeleportTask(bot, null);
        tpTask.setParams(tpParams);

        add(tpTask);
    }

}
