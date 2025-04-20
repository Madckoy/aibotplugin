package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.calibration.BotCalibrationTask;
import com.devone.bot.core.bot.task.active.calibration.params.BotCalibrationTaskParams;
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

        BotCalibrationTaskParams tpParams = new BotCalibrationTaskParams();
        BotCalibrationTask tpTask = new BotCalibrationTask(bot);
        tpTask.setParams(tpParams);

        add(tpTask);
    }

}
