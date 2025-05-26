package com.devone.bot.core.brain.cortex.sequence;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerFishInWaterParams;
import com.devone.bot.core.task.active.fishing.BotFishingTask;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;
import java.util.ArrayList;
import java.util.List;

public class BotSequenceContainerFishInWater extends BotSequenceContainer<BotSequenceContainerFishInWaterParams> {

    public BotSequenceContainerFishInWater(Bot bot) {
        super(bot, BotSequenceContainerFishInWaterParams.class);
        setIcon("🔀");
        setObjective("Sequence: Catch Fish If Standing in Water");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " 🎯 Условие сработало: ловим рыбу.");

        BotFishingTask fishingTask = new BotFishingTask(bot);
        fishingTask.setParams(fishingTask.getParams());

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(fishingTask);

        return subtasks;
    }
}
