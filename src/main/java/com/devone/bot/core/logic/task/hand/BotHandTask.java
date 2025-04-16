package com.devone.bot.core.logic.task.hand;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public abstract class BotHandTask extends BotTask<BotHandTaskParams> {

    private BotBlockData target;

    public BotHandTask(Bot bot) {
        super(bot);
        setParams(new BotHandTaskParams()); // загружаем конфиг
    }

    @Override
    public IBotTaskParameterized<BotHandTaskParams> setParams(BotHandTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        this.target = params.getTarget();

        if (target != null) {
            bot.getRuntimeStatus().setTargetLocation(target);
            BotLogger.info("✅", isLogging(), bot.getId() + " Target for BotHandTask is set: " + target);
        } else {
            BotLogger.info("⚠️", isLogging(), bot.getId() + " Target is null in BotHandTask.");
        }

        return this;
    }

    @Override
    public void execute() {
        BotLogger.info("🔶", isLogging(), bot.getId() + " Executing BotHandTask...");

        if (target == null) {
            BotLogger.info("❌", isLogging(), bot.getId() + " BotHandTask: Target is null.");
            this.stop();
            return;
        }

        // Конкретная логика — в наследнике
    }

    @Override
    public void stop() {
        bot.getRuntimeStatus().setTargetLocation(null);
        super.stop();
    }

    public BotBlockData getTarget() {
        return target;
    }
}
