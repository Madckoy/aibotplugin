package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;

import org.bukkit.entity.Player;


public class BotTaskProtect extends BotTaskPlayerLinked {

    public BotTaskProtect(Bot bot, Player player) {
        super(bot, player, "PROTECT");
    }

    @Override
    public void executeTask() {
    
    }
}
