package com.devone.bot.core.task.active.calibrate;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.calibrate.params.BotCalibrateTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotCalibrateTask extends BotTaskAutoParams<BotCalibrateTaskParams> {

    private String message="";

    public BotCalibrateTask(Bot bot, String msg) {
        super(bot, BotCalibrateTaskParams.class);
        message = msg;
    }

    @Override
    public IBotTaskParameterized<BotCalibrateTaskParams> setParams(BotCalibrateTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        setEnabled(params.isEnabled());
        return this;
    }

    @Override
    public void execute() {

        long rmt = BotUtils.getRemainingTime(startTime, params.getTimeout());

        setObjective(params.getObjective() + " " + message + " (" + rmt + ")");

        //BotMemoryV2Utils.clearAllVisited(bot);

        //BotLogger.debug(icon, isLogging(), bot.getId() + " 🗑️ Removed all visited navigation points");

        //bot.getNavigator().resetStuckCount();

        if (rmt <= 0) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Task timeout passed. Ending Task.");
            stop();
        }
    }
}
