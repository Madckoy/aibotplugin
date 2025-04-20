package com.devone.bot.core.bot.task.active.calibration.params;

import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotCalibrationTaskParams extends BotTaskParams {

    private long timeout = BotConstants.DEFAULT_CALIBRATION_TIMEOUT;

    public BotCalibrationTaskParams() {
        super();

        setIcon("🛠️");
        setObjective("Calibration");
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
