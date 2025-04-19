package com.devone.bot.core.bot.task.active.hand;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.hand.params.BotHandTaskParams;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public abstract class BotHandTask<T extends BotHandTaskParams> extends BotTaskAutoParams<T> {

    private BotBlockData target;

    public BotHandTask(Bot bot, Class<T> paramClass) {
        super(bot, paramClass);
    }

    @Override
    public IBotTaskParameterized<T> setParams(T params) {
        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());
        this.target = params.getTarget();

        if (target != null) {
            bot.getNavigation().setTarget(target);
            BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Target for BotHandTask is set: " + target);
        } else {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⚠️ Target is null in BotHandTask.");
        }

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🔶 Executing BotHandTask...");

        if (target == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ BotHandTask: Target is null.");
            this.stop();
            return;
        }
        turnToTarget(this, target);

        // Конкретная логика — в наследнике
    }

    @Override
    public void stop() {
        bot.getNavigation().setTarget(null);
        super.stop();
    }

    public BotBlockData getTarget() {
        return target;
    }
}
