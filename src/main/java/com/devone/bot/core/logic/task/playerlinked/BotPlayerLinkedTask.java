package com.devone.bot.core.logic.task.playerlinked;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;

public abstract class BotPlayerLinkedTask extends BotTask {

    protected Player player;

    public BotPlayerLinkedTask(Bot bot, Player player) {
        super(bot, player);
    }

}
