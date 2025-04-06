package com.devone.bot.core.logic.tasks;

import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;

public abstract class BotPlayerLinkedTask extends BotTask {

    protected Player player;

    public BotPlayerLinkedTask(Bot bot, Player player, String name) {
        super(bot, player, name);
    }

}
