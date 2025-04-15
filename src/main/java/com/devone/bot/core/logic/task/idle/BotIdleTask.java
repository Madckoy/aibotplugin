package com.devone.bot.core.logic.task.idle;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.idle.params.BotIdleTaskParams;


public class BotIdleTask extends BotTask {

    BotIdleTaskParams params = new BotIdleTaskParams();

    public BotIdleTask(Bot bot) {
        super(bot);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
    }

    @Override
    public void execute() {
        this.stop();
    }

}
