package com.devone.bot.core.task.passive.active.playerlinked;

import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.params.BotTaskParams;


public abstract class BotPlayerLinkedTask<T extends BotTaskParams> extends BotTaskAutoParams<T> {

    protected final Player player;

    public BotPlayerLinkedTask(Bot bot, Player player, Class<T> paramClass) {
        super(bot, player, paramClass);
        this.player = player;
    }
}
