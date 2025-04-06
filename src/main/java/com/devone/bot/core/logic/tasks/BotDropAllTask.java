package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotInventory;
import com.devone.bot.core.logic.tasks.configs.BotDropAllTaskConfig;

public class BotDropAllTask extends BotPlayerLinkedTask {


    public BotDropAllTask(Bot bot, Player player) {
        super(bot, player, "📦↴");
        
        BotDropAllTaskConfig config = new BotDropAllTaskConfig();
        this.isLogged  = config.isLogged();

        bot.getRuntimeStatus().setTargetLocation(new Location (Bukkit.getWorlds().get(0), config.getX(),
                                                                  config.getY(), 
                                                                  config.getZ()));

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