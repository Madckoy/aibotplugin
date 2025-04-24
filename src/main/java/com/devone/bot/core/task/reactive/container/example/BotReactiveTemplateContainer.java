package com.devone.bot.core.task.reactive.container.example;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.reactive.container.params.example.BotReactiveTemplateContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveTemplateContainer extends BotReactiveTaskContainer<BotReactiveTemplateContainerParams> {

    public BotReactiveTemplateContainer(Bot bot) {
        super(bot, BotReactiveTemplateContainerParams.class);
        setIcon("📦");
        setObjective("Шаблон реактивного контейнера");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " 📦 enqueue() шаблонного контейнера");

        // ✅ Используем реактивный сахар внутри
        // bot.pushReactiveTask(new YourTask(bot));
    }
}
