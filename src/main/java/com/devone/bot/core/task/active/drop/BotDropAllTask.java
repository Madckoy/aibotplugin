package com.devone.bot.core.task.active.drop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.drop.params.BotDropAllTaskParams;
import com.devone.bot.core.task.active.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotDropAllTask extends BotPlayerLinkedTask<BotDropAllTaskParams> {

    public BotDropAllTask(Bot bot, Player pl) {
        super(bot, pl, BotDropAllTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotDropAllTaskParams> setParams(BotDropAllTaskParams params) {
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        setEnabled(params.isEnabled());
        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogged(), bot.getId()+ " 🎁 Dropping all loot... ");
        
        BotInventory.dropAllItems(bot);

        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {

            stop();

        }, 200); // например, 60L = 3 сек       
    }

    @Override
    public void stop() {
        BotLogger.debug(icon, isLogged(), bot.getId() + " ✅ Drop task completed");
        done=true;
        super.stop();
    }
}