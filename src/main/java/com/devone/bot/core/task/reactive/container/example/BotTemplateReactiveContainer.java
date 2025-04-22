package com.devone.bot.core.task.reactive.container.example;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.reactive.container.params.example.BotTemplateReactiveContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTemplateReactiveContainer extends BotReactiveTaskContainer<BotTemplateReactiveContainerParams> {

    public BotTemplateReactiveContainer(Bot bot) {
        super(bot, BotTemplateReactiveContainerParams.class);
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
