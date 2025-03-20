package com.devone.aibot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.logic.tasks.configs.BotTaskDropAllConfig;

public class BotTaskDropAll extends BotTaskPlayerLinked {


    public BotTaskDropAll(Bot bot, Player player) {
        super(bot, player, "📦");
        
        BotTaskDropAllConfig config = new BotTaskDropAllConfig();

        targetLocation = new Location (Bukkit.getWorlds().get(0), config.getX(),
                                                                  config.getY(), 
                                                                  config.getZ());
        
    }

    @Override
    public void executeTask() {
        
        BotInventory.dropAllItems(bot);

        isDone = true;
    }

}