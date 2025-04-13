package com.devone.bot.core.logic.task.hand;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotHandTask extends BotTask {

    private BotBlockData target;
    private boolean isLogged = true;

    public BotHandTask(Bot bot) {
        super(bot, "✋🏻");
        setObjective("Hit the target");
    }

    @Override
    public BotHandTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotHandTaskParams handParams) {
            this.target = handParams.getTarget();
            this.isLogged = handParams.isLogged();
            bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());
        } else {
            BotLogger.info(isLogged, bot.getId() + " ❌ Invalid parameters for BotHandTask.");
            this.stop();
        }
        return this;
    }


    public void execute() {
        if (target == null) {
            BotLogger.info(isLogged, bot.getId() + " ❌ Target is null.");
            this.stop();
            return;
        }
    };

    @Override
    public void stop() {
        isDone = true;
        bot.getRuntimeStatus().setTargetLocation(null);
    }

}
