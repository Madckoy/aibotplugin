package com.devone.bot.core.logic.task.hand;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.hand.params.BotHandTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public abstract class BotHandTask extends BotTask {

    private BotBlockData target;
    private boolean isLogged = true;

    public BotHandTask(Bot bot) {
        super(bot, "✋🏻");
        setObjective("Hit the target");
    }

    @Override
    public BotHandTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);
        this.target = ((BotHandTaskParams)params).getTarget();
        this.isLogged = ((BotHandTaskParams)params).isLogged();
        bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());
        BotLogger.info(isLogged, bot.getId() + " ✅ Parameters for BotHandTask set.");
        BotLogger.info(isLogged, bot.getId() + " BotHandTaskParams: " + params);   
        return this;
    }


    public void execute() {
        BotLogger.info(isLogged, bot.getId() + " 🔶 Executing BotHandTask");
        
        if (target == null) {
            BotLogger.info(isLogged, bot.getId() + " ❌ BotHandTask: Target is null.");
            this.stop();
        }
    };

    @Override
    public void stop() {
        isDone = true;
        bot.getRuntimeStatus().setTargetLocation(null);
    }

}
