package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceCalibrateParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceCalibrate extends BotSequenceContainer<BotSequenceCalibrateParams> {

    public BotSequenceCalibrate(Bot bot) {

        super(bot, BotSequenceCalibrateParams.class);
        //setIcon("<");
        setObjective("Sequence: Bot Calibration Task");
        //setDeffered(true);
    }

    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());

        BotCalibrateTask tpTask = new BotCalibrateTask(bot, "Initialize...");

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tpTask);
        
        return subtasks;
    }

}
