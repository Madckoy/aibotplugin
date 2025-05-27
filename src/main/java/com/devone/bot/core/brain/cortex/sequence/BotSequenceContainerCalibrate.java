package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerCalibrateParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceContainerCalibrate extends BotSequenceContainer<BotSequenceContainerCalibrateParams> {

    public BotSequenceContainerCalibrate(Bot bot) {

        super(bot, BotSequenceContainerCalibrateParams.class);
        setIcon("🔀");
        setObjective("Sequence: Bot Calibration Task");
        setDeffered(true);
    }

    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());

        BotCalibrateTask tpTask = new BotCalibrateTask(bot, "Initialize...");
        tpTask.setParams(tpTask.getParams());

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tpTask);
        
        return subtasks;
    }

}
