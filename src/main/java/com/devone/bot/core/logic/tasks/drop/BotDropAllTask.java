package com.devone.bot.core.logic.tasks.drop;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.logic.tasks.drop.config.BotDropAllTaskConfig;
import com.devone.bot.core.logic.tasks.playerlinked.BotPlayerLinkedTask;

public class BotDropAllTask extends BotPlayerLinkedTask {


    public BotDropAllTask(Bot bot, Player player) {
        super(bot, player, "📦↴");
        
        BotDropAllTaskConfig config = new BotDropAllTaskConfig();
        this.isLogged  = config.isLogged();

        setObjective("Drop off the loot");

        
    }

    @Override
    public void execute() {
        
        BotInventory.dropAllItems(bot);

        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;
    }

}