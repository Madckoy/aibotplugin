package com.devone.bot.core.bot.brain.logic.task.playerlinked;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.task.BotTaskAutoParams;
import com.devone.bot.core.bot.brain.logic.task.params.BotTaskParams;



public abstract class BotPlayerLinkedTask<T extends BotTaskParams> extends BotTaskAutoParams<T> {

    protected final Player player;

    public BotPlayerLinkedTask(Bot bot, Player player, Class<T> paramClass) {
        super(bot, player, paramClass);
        this.player = player;
    }
}
