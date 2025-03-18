package com.devone.aibot.core.logic.tasks;

import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;

public class BotTaskDropAll extends BotTaskPlayerLinked {

    public BotTaskDropAll(Bot bot, Player player) {
        super(bot, player, "📦");
    }

    @Override
    public void executeTask() {
        BotInventory.dropAllItems(bot);
        isDone = true;
    }

}