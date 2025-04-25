package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveTeleportToPositionAndExcavateContainerParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveTeleportToPositionAndExcavateContainer
        extends BotReactiveTaskContainer<BotReactiveTeleportToPositionAndExcavateContainerParams> {

    private final BotPosition position;

    public BotReactiveTeleportToPositionAndExcavateContainer(Bot bot, BotPosition position) {
        super(bot, BotReactiveTeleportToPositionAndExcavateContainerParams.class);
        this.position = position;
        setObjective("Reactive: Bot Teleport Task and Bot Excavate Task");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotTeleportTask tp = new BotTeleportTask(bot, null);
        BotTeleportTaskParams params = new BotTeleportTaskParams();
        params.setPosition(position);
        tp.setParams(params);
        add(tp);

        BotExcavateTaskParams excvParams = new BotExcavateTaskParams();
        BotExcavateTask excvTask = new BotExcavateTask(bot);
        excvTask.setParams(excvParams);
        add(excvTask);

    }
}
