package com.devone.bot.core.task.reactive.container.example;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.reactive.container.params.example.BotCustomReactiveContainerParams;

public class BotCustomReactiveContainer extends BotReactiveTaskContainer<BotCustomReactiveContainerParams> {

    public BotCustomReactiveContainer(Bot bot) {
        super(bot, BotCustomReactiveContainerParams.class);
        setIcon("⚙️");
        setObjective("Пользовательская реакция на низкое здоровье");
    }

    @Override
    protected void enqueue(Bot bot) {
        // Заменить на нужные тебе действия:
        // bot.pushReactiveTask(new BotmyCustimTask(bot)); // for example
    }
}
