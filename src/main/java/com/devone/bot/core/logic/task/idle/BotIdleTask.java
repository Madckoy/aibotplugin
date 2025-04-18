package com.devone.bot.core.logic.task.idle;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.idle.params.BotIdleTaskParams;
import com.devone.bot.core.plugin.AIBotPlugin;
import com.devone.bot.utils.logger.BotLogger;
import org.bukkit.scheduler.BukkitRunnable;

public class BotIdleTask extends BotTaskAutoParams<BotIdleTaskParams> {

    private boolean isWaiting = false;

    public BotIdleTask(Bot bot) {
        super(bot, BotIdleTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotIdleTaskParams> setParams(BotIdleTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        if (isWaiting) return; // ✅ уже в режиме ожидания

        isWaiting = true;

        long delayTicks = params.getTimeout(); // уже в тиках
        BotLogger.debug("🍹", isLogging(), bot.getId() + " Entering idle mode for " + delayTicks + " ticks.");

        new BukkitRunnable() {
            @Override
            public void run() {
                BotLogger.debug("✅", isLogging(), bot.getId() + " Idle timeout finished.");
                isWaiting = false; // (на всякий случай)
                stop();
            }
        }.runTaskLater(AIBotPlugin.getInstance(), delayTicks);
    }
}
