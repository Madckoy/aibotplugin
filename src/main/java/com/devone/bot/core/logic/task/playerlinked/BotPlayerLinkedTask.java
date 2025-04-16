package com.devone.bot.core.logic.task.playerlinked;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.params.BotTaskParams;

public abstract class BotPlayerLinkedTask<T extends BotTaskParams> extends BotTask<T> {

    protected final Player player;

    public BotPlayerLinkedTask(Bot bot, Player player) {
        super(bot, player);
        this.player = player;
    }
}
