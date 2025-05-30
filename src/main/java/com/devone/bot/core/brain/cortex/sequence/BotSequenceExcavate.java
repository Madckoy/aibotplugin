package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceExcavateParams;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceExcavate extends BotSequenceContainer<BotSequenceExcavateParams> {

    public BotSequenceExcavate(Bot bot) {

        super(bot, BotSequenceExcavateParams.class);
        setObjective("Sequence: Excavate Task");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());

        BotExcavateTask excvTask = new BotExcavateTask(bot);
        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(excvTask);

        return subtasks;
    }

}
