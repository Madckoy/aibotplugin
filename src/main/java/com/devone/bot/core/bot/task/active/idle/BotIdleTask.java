package com.devone.bot.core.bot.task.active.idle;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.idle.params.BotIdleTaskParams;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotIdleTask extends BotTaskAutoParams<BotIdleTaskParams> {

    public BotIdleTask(Bot bot) {
        super(bot, BotIdleTaskParams.class);
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
        long timeout = params.getTimeout();
        
        if (getElapsedTime() >= timeout) {
            BotLogger.debug("✅", isLogging(), bot.getId() + " Idle timeout passed. Ending idle.");
            stop();
        } else {
            long remaining = timeout - getElapsedTime();
            setObjective("Idle: " + remaining + " ticks");
        }
    }
}
