package com.devone.bot.core.brain.cortex.sequence.example;

import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.example.BotSequenceContainerTemplateParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceContainerTemplate extends BotSequenceContainer<BotSequenceContainerTemplateParams> {

    public BotSequenceContainerTemplate(Bot bot) {
        super(bot, BotSequenceContainerTemplateParams.class);
        setIcon("🔀");
        setObjective("Шаблон реактивного контейнера");
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " #️⃣ enqueue() шаблонного контейнера");

        // ✅ Используем реактивный сахар внутри
        // bot.pushReactiveTask(new YourTask(bot));
        return null;
    }
}
