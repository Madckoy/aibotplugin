package com.devone.bot.core.task.passive.active.calibrate;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.MemoryType;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.passive.active.calibrate.params.BotCalibrateTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotCalibrateTask extends BotTaskAutoParams<BotCalibrateTaskParams> {

    public BotCalibrateTask(Bot bot) {
        super(bot, BotCalibrateTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotCalibrateTaskParams> setParams(BotCalibrateTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {

        long rmt = BotUtils.getRemainingTime(startTime, params.getTimeout());

        setObjective(params.getObjective() + " (" + rmt + ")");

        bot.getBrain().getMemory().cleanup(MemoryType.VISITED_BLOCKS);

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🗑️ Removed all visited navigation points");

        bot.getNavigation().resetStuckCount();

        if (rmt <= 0) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Task timeout passed. Ending Task.");
            stop();
        }
    }
}
