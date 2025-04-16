package com.devone.bot.core.logic.task;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.params.BotTaskParams;

public abstract class BotTaskAutoParams<T extends BotTaskParams> extends BotTask<T> {

    // С Player
    public BotTaskAutoParams(Bot bot, Player pl, Class<T> paramClass) {
        super(bot, pl);
        setParams(BotTaskParams.loadOrCreate(paramClass));
    }

    // Без Player
    public BotTaskAutoParams(Bot bot, Class<T> paramClass) {
        this(bot, null, paramClass);
    }

    // С кастомными параметрами (и Player)
    public BotTaskAutoParams(Bot bot, Player pl, T explicitParams) {
        super(bot, pl);
        setParams(explicitParams);
    }

    // С кастомными параметрами (без Player)
    public BotTaskAutoParams(Bot bot, T explicitParams) {
        this(bot, null, explicitParams);
    }
}