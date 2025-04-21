package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.bot.task.active.calibrate.params.BotCalibrateTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotCalibrateReactiveContainerParams;

import com.devone.bot.core.utils.logger.BotLogger;

public class BotCalibrateReactiveContainer extends BotReactiveTaskContainer<BotCalibrateReactiveContainerParams> {

    public BotCalibrateReactiveContainer(Bot bot) {

        super(bot, BotCalibrateReactiveContainerParams.class);

        setObjective("Reactive: BotCalibrationTask");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotCalibrateTaskParams tpParams = new BotCalibrateTaskParams();
        BotCalibrateTask tpTask = new BotCalibrateTask(bot);
        tpTask.setParams(tpParams);

        add(tpTask);
    }

}
