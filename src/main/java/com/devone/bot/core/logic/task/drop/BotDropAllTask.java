package com.devone.bot.core.logic.task.drop;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.logic.task.drop.params.BotDropAllTaskParams;
import com.devone.bot.core.logic.task.playerlinked.BotPlayerLinkedTask;

public class BotDropAllTask extends BotPlayerLinkedTask {

    BotDropAllTaskParams params = new BotDropAllTaskParams();

    public BotDropAllTask(Bot bot, Player pl) {
        super(bot, pl);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
    }

    @Override
    public void execute() {
        
        BotInventory.dropAllItems(bot);

        this.stop();
    }
}