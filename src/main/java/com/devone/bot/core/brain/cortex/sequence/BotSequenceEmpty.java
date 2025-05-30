package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceEmptyParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceEmpty
        extends BotSequenceContainer<BotSequenceEmptyParams> {

    public BotSequenceEmpty(Bot bot) {
        super(bot, BotSequenceEmptyParams.class);
        //setIcon("🔀");
        setObjective("Sequence: Empty");
        //setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());
                List<BotTask<?>> subtasks = new ArrayList<>();
        return subtasks;
    }
}
