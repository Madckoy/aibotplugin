package com.devone.bot.core.task.reactive.container;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.task.active.calibrate.params.BotCalibrateTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveCalibrateContainerParams;

import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveCalibrateContainer extends BotReactiveContainer<BotReactiveCalibrateContainerParams> {

    public BotReactiveCalibrateContainer(Bot bot) {

        super(bot, BotReactiveCalibrateContainerParams.class);
        setIcon("🔀");
        setObjective("Reactive: Bot Calibration Task");
        setDeffered(true);
    }

    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotCalibrateTaskParams tpParams = new BotCalibrateTaskParams();
        BotCalibrateTask tpTask = new BotCalibrateTask(bot);
        tpTask.setParams(tpParams);

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tpTask);
        
        return subtasks;
    }

}
