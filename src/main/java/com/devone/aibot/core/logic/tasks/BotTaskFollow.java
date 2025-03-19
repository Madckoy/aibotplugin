package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;

import org.bukkit.entity.Player;

public class BotTaskFollow extends BotTaskPlayerLinked {


    public BotTaskFollow(Bot bot, Player player) {
        super(bot, player, "⛶");
    }


    @Override
    public void executeTask() {
       //do nothing
    }

}
