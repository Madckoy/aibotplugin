package com.devone.bot.core.bot.brain.logic.task.hand;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.task.BotTaskAutoParams;
import com.devone.bot.core.bot.brain.logic.task.IBotTaskParameterized;
import com.devone.bot.core.bot.brain.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;

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
            BotLogger.debug("✅", isLogging(), bot.getId() + " Target for BotHandTask is set: " + target);
        } else {
            BotLogger.debug("⚠️", isLogging(), bot.getId() + " Target is null in BotHandTask.");
        }

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug("🔶", isLogging(), bot.getId() + " Executing BotHandTask...");

        if (target == null) {
            BotLogger.debug("❌", isLogging(), bot.getId() + " BotHandTask: Target is null.");
            this.stop();
            return;
        }

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
