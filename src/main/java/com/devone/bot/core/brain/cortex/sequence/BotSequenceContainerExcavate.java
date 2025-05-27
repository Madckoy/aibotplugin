package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerExcavateParams;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceContainerExcavate extends BotSequenceContainer<BotSequenceContainerExcavateParams> {

    public BotSequenceContainerExcavate(Bot bot) {

        super(bot, BotSequenceContainerExcavateParams.class);
        setIcon("🔀");
        setObjective("Sequence: Excavate Tasks");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());

        BotExcavateTask excvTask = new BotExcavateTask(bot);
        excvTask.setParams(excvTask.getParams());

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(excvTask);

        
        BotCalibrateTask calibrateTask = new BotCalibrateTask(bot);
        subtasks.add(calibrateTask);

        return subtasks;
    }

}
