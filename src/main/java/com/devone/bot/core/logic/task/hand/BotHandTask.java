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
    private BotHandTaskParams params = new BotHandTaskParams();

    public BotHandTask(Bot bot) {
        super(bot);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
    }

    @Override
    public BotHandTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);
        this.params.copyFrom(params);
        setIcon(this.params.getIcon());
        setObjective(this.params.getObjective());
        this.target = ((BotHandTaskParams)params).getTarget();
        bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());
        BotLogger.info(isLogging(), bot.getId() + " ✅ Parameters for BotHandTask set.");
        BotLogger.info(isLogging(), bot.getId() + " BotHandTaskParams: " + params);   
        return this;
    }


    public void execute() {
        BotLogger.info(isLogging(), bot.getId() + " 🔶 Executing BotHandTask");
        
        if (target == null) {
            BotLogger.info(isLogging(), bot.getId() + " ❌ BotHandTask: Target is null.");
            this.stop();
        }
    };

    @Override
    public void stop() {
        bot.getRuntimeStatus().setTargetLocation(null);
        super.stop();
    }

}
