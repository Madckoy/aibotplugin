package com.devone.bot.core.logic.task.idle;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.idle.params.BotIdleTaskParams;

public class BotIdleTask extends BotTask<BotIdleTaskParams> {

    public BotIdleTask(Bot bot) {
        super(bot);
        setParams(new BotIdleTaskParams());
    }

    @Override
    public IBotTaskParameterized<BotIdleTaskParams> setParams(BotIdleTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        this.stop();
    }
}
