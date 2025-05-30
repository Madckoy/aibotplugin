package com.devone.bot.core.task.active.hand;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.hand.params.BotHandTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public abstract class BotHandTask<T extends BotHandTaskParams> extends BotTaskAutoParams<T> {

    private BotBlockData target;

    public void setTarget(BotBlockData target) {
        this.target = target;
    }

    public BotHandTask(Bot bot, Class<T> paramClass) {
        super(bot, paramClass);
    }

    @Override
    public IBotTaskParameterized<T> setParams(T params) {
        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🔶 Executing BotHandTask...");

        if (getTarget() == null) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ BotHandTask: Target is null.");
            this.stop();
            return;
        }

        bot.getNavigator().setTarget(target);
    }

    @Override
    public void stop() {
        bot.getNavigator().setTarget(null);
        super.stop();
    }

    public BotBlockData getTarget() {
        return target;
    }
}
