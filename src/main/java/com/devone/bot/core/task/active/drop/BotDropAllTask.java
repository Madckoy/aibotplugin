package com.devone.bot.core.task.active.drop;

import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.drop.params.BotDropAllTaskParams;
import com.devone.bot.core.task.active.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotDropAllTask extends BotPlayerLinkedTask<BotDropAllTaskParams> {

    BotDropAllTaskParams params = new BotDropAllTaskParams();

    public BotDropAllTask(Bot bot, Player pl) {
        super(bot, pl, BotDropAllTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotDropAllTaskParams> setParams(BotDropAllTaskParams params) {
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogging(), bot.getId()+ " 🎁 Dropping all loot... ");
        BotInventory.dropAllItems(bot);

        this.stop();
    }
}