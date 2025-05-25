package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerTeleportToPositionParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceContainerTeleportToPosition
        extends BotSequenceContainer<BotSequenceContainerTeleportToPositionParams> {

    private final BotPosition position;

    public BotSequenceContainerTeleportToPosition(Bot bot, BotPosition position) {
        super(bot, BotSequenceContainerTeleportToPositionParams.class);
        this.position = position;
        setIcon("🔣");
        setObjective("Reactive: Bot Teleport Task");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());

        BotTeleportTask tp = new BotTeleportTask(bot, null);
        BotTeleportTaskParams params = new BotTeleportTaskParams();
        params.setPosition(position);
        tp.setParams(params);
        
        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tp);
        return subtasks;
    }
}
