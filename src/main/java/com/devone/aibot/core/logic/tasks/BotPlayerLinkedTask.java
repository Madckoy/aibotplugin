package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import org.bukkit.entity.Player;

public abstract class BotPlayerLinkedTask extends BotTask {

    protected Player player;

    public BotPlayerLinkedTask(Bot bot, Player player, String name) {
        super(bot, player, name);
    }

}
