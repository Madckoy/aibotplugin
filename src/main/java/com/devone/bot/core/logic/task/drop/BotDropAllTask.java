package com.devone.bot.core.logic.task.drop;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.drop.params.BotDropAllTaskParams;
import com.devone.bot.core.logic.task.playerlinked.BotPlayerLinkedTask;

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
        
        BotInventory.dropAllItems(bot);

        this.stop();
    }
}