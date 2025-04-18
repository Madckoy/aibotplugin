package com.devone.bot.core.bot.behaviour.task.drop;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.behaviour.task.IBotTaskParameterized;
import com.devone.bot.core.bot.behaviour.task.drop.params.BotDropAllTaskParams;
import com.devone.bot.core.bot.behaviour.task.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.bot.inventory.BotInventory;

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