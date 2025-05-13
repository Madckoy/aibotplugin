package com.devone.bot.core.task.active.swim;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.swim.params.BotSwimTaskParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSwimTask extends BotTaskAutoParams<BotSwimTaskParams> {

    private BotPosition target;

    public BotSwimTask(Bot bot) {
        super(bot, BotSwimTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotSwimTaskParams> setParams(BotSwimTaskParams params) {
        super.setParams(params);
        this.target = params.getPosition();
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (this.target == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Target for swim is not set.");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (this.target == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Target is null. Stopping.");
            stop();
            return;
        }

        setObjective(params.getObjective() + " to: " + target.toCompactString());
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🌊 Start swimming to " + target);

        bot.getNavigator().setTarget(target.toBlockData());
        boolean canNavigate = bot.getNavigator().navigate(1.2f); // скорость чуть ниже обычной

        if (!canNavigate) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Failed to start swim navigation.");
        }

        stop(); // задача однократного действия
    }
}
