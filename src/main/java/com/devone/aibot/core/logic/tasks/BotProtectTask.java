package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;

import org.bukkit.entity.Player;


public class BotProtectTask extends BotPlayerLinkedTask {

    public BotProtectTask(Bot bot, Player player) {
        super(bot, player, "🛡️");
        setObjective("Protect");
    }

    @Override
    public void executeTask() {
    
    }

    @Override
    public boolean isLogged() {
        return this.isLogged;
    }
}
