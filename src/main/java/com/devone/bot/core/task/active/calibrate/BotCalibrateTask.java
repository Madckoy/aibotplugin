package com.devone.bot.core.task.active.calibrate;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.brain.navigator.simulator.BotSimulatorResult;
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

        BotLogger.debug(icon, isLogged(), bot.getId() + " 🗑️ Remove all visited navigation points");
        BotMemoryV2Utils.clearAllVisited(bot);

        BotLogger.debug(icon, isLogged(), bot.getId() + " 🗑️ Reset Watchdog");

        bot.getNavigator().resetStuckCount();
        bot.getBrain().getMemoryV2()
            .partition("WATCHDOG", BotMemoryV2Partition.Type.MAP)
            .remove("POSITION");

        bot.getBrain().getMemoryV2()
            .partition("WATCHDOG", BotMemoryV2Partition.Type.MAP)
            .remove("TIME");
        


        try {

            BotLogger.debug(icon, isLogged(), bot.getId() + " 🗑️ Reset Navigation");
            bot.getNavigator().setEnabled(true);
            BotSimulatorResult res = bot.getNavigator().simulate(BotConstants.DEFAULT_MAX_SIGHT_FOV, BotConstants.DEFAULT_SCAN_RADIUS, BotConstants.DEFAULT_SCAN_HEIGHT);            
            float bestYaw = res.yaw;
            int   reachables = res.reachables;
            message = "Best yaw: "+ bestYaw + " with Reachable: " + reachables;
            stop();
        } catch (Exception e) {
            message = "Navigator reset failure!";
        }
        
        setObjective(params.getObjective() + " " + message + " (" + rmt + ")");

        if (rmt <= 0) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Task timeout passed. Ending Task.");
            stop();
        }
    }
}
