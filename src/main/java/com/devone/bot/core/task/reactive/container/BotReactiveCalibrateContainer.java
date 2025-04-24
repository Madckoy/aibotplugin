package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.task.active.calibrate.params.BotCalibrateTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveCalibrateContainerParams;

import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveCalibrateContainer extends BotReactiveTaskContainer<BotReactiveCalibrateContainerParams> {

    public BotReactiveCalibrateContainer(Bot bot) {

        super(bot, BotReactiveCalibrateContainerParams.class);

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
