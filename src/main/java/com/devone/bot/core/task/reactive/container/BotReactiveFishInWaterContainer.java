package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.fishing.BotFishingTask;
import com.devone.bot.core.task.active.fishing.params.BotFishingTaskParams;
import com.devone.bot.core.task.passive.BotReactiveContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.reactive.container.params.BotReactiveFishInWaterParams;
import com.devone.bot.core.utils.logger.BotLogger;
import java.util.ArrayList;
import java.util.List;

public class BotReactiveFishInWaterContainer extends BotReactiveContainer<BotReactiveFishInWaterParams> {

    public BotReactiveFishInWaterContainer(Bot bot) {
        super(bot, BotReactiveFishInWaterParams.class);
        setIcon("🎣");
        setObjective("Reactive: Catch Fish If Standing in Water");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " 🎯 Условие сработало: ловим рыбу.");

        BotFishingTaskParams params = new BotFishingTaskParams();
        params.setObjective("Ловим рыбу руками");
        params.setIcon("🎣");

        BotFishingTask fishingTask = new BotFishingTask(bot);
        fishingTask.setParams(params);

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(fishingTask);

        return subtasks;
    }
}
