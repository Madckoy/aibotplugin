package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.idle.BotIdleTask;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.example.BotCustomReactionContainerParams;

public class BotCustomReactionContainer extends BotReactiveTaskContainer<BotCustomReactionContainerParams> {

    public BotCustomReactionContainer(Bot bot) {
        super(bot, BotCustomReactionContainerParams.class);
        setIcon("⚙️");
        setObjective("Пользовательская реакция на низкое здоровье");
    }

    @Override
    protected void enqueue(Bot bot) {
        // Заменить на нужные тебе действия:
        bot.reactiveTaskStart(new BotIdleTask(bot)); // for example
    }
}
