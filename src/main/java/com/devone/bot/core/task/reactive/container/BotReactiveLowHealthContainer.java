package com.devone.bot.core.task.reactive.container;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveLowHealthContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotReactiveLowHealthContainer extends BotReactiveTaskContainer<BotReactiveLowHealthContainerParams> {

    public BotReactiveLowHealthContainer(Bot bot) {
        super(bot, BotReactiveLowHealthContainerParams.class);
        setIcon("🔀");
        setObjective("Reactive: Bot Teleport Task on Low HP");
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
        tpParams.setPosition(BotWorldHelper.getWorldSpawnLocation());

        BotTeleportTask tpTask = new BotTeleportTask(bot, null);
        tpTask.setParams(tpParams);

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tpTask);
        
        return subtasks;
    }

}
