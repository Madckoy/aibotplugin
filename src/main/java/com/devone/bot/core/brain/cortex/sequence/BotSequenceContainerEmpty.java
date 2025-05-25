package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerEmptyParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceContainerEmpty
        extends BotSequenceContainer<BotSequenceContainerEmptyParams> {

    public BotSequenceContainerEmpty(Bot bot) {
        super(bot, BotSequenceContainerEmptyParams.class);
        setIcon("🔣");
        setObjective("Reactive: Empty container");
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());
                List<BotTask<?>> subtasks = new ArrayList<>();
        return subtasks;
    }
}
