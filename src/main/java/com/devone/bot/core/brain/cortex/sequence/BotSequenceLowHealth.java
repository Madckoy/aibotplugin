package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceLowHealthParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotSequenceLowHealth extends BotSequenceContainer<BotSequenceLowHealthParams> {

    public BotSequenceLowHealth(Bot bot) {
        super(bot, BotSequenceLowHealthParams.class);
        setIcon("🔀");
        setObjective("Sequence: Bot Teleport Task on Low HP");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());

        BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
        tpParams.setPosition(BotWorldHelper.getWorldSpawnLocation());

        BotTeleportTask tpTask = new BotTeleportTask(bot, null);
        tpTask.setParams(tpParams);

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tpTask);
        
        return subtasks;
    }

}
