package com.devone.bot.core.task.reactive.container;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.passive.BotReactiveContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;

import com.devone.bot.core.task.reactive.container.params.BotReactiveExcavateContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveExcavateContainer extends BotReactiveContainer<BotReactiveExcavateContainerParams> {

    public BotReactiveExcavateContainer(Bot bot) {

        super(bot, BotReactiveExcavateContainerParams.class);
        setIcon("#️⃣");
        setReactive(true);
        setObjective("Reactive: Excavate Tasks");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
        BotExcavateTaskParams excvParams = new BotExcavateTaskParams();
        BotExcavateTask excvTask = new BotExcavateTask(bot);
        excvTask.setReactive(true);
        excvTask.setParams(excvParams);

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(excvTask);
        
        return subtasks;
    }

}
