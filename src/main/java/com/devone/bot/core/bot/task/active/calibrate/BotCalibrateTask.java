package com.devone.bot.core.bot.task.active.calibrate;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.memory.MemoryType;
import com.devone.bot.core.bot.task.active.calibrate.params.BotCalibrateTaskParams;
import com.devone.bot.core.bot.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
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

        long rmt = BotUtils.getRemainingTime(startTime);

        setObjective(params.getObjective() + " (" + rmt + ")");

        bot.getBrain().getMemory().cleanup(MemoryType.VISITED_BLOCKS);

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🗑️ Removed all visited navigation points");

        bot.getNavigation().resetStuckCount();

        //if (rmt <= 0) {
        //    BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Task timeout passed. Ending Task.");
        //    stop();
        //}

        stop();
    }
}
