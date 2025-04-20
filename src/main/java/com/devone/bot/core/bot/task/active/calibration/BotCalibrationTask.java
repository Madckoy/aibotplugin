package com.devone.bot.core.bot.task.active.calibration;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.memory.MemoryType;
import com.devone.bot.core.bot.task.active.calibration.params.BotCalibrationTaskParams;
import com.devone.bot.core.bot.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotCalibrationTask extends BotTaskAutoParams<BotCalibrationTaskParams> {

    public BotCalibrationTask(Bot bot) {
        super(bot, BotCalibrationTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotCalibrationTaskParams> setParams(BotCalibrationTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {

        long rmt = BotUtils.getRemainingTime(startTime);

        setObjective(params.getObjective() + " / Performing maintenance " + " (" + rmt + ")");

        bot.getBrain().getMemory().cleanup(MemoryType.VISITED_BLOCKS);

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🗑️ Removed all visited navigation points");

        bot.getState().resetStuckCount();

        BotSonar3DTask sonar = new BotSonar3DTask(bot);
        sonar.execute();

        if (rmt <= 0) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Task timeout passed. Ending Task.");
            stop();
        }
    }
}
