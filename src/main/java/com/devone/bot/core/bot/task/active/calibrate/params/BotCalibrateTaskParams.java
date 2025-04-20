package com.devone.bot.core.bot.task.active.calibrate.params;

import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotCalibrateTaskParams extends BotTaskParams {

    private long timeout = BotConstants.DEFAULT_CALIBRATION_TIMEOUT;

    public BotCalibrateTaskParams() {
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
