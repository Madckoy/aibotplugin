package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.example.BotTemplateReactionContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTemplateReactionContainer extends BotReactiveTaskContainer<BotTemplateReactionContainerParams> {

    public BotTemplateReactionContainer(Bot bot) {
        super(bot, BotTemplateReactionContainerParams.class);
        setIcon("📦");
        setObjective("Шаблон реактивного контейнера");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " 📦 enqueue() шаблонного контейнера");

        // ✅ Используем реактивный сахар внутри
        // bot.reactiveTaskStart(new YourTask(bot));
    }
}
