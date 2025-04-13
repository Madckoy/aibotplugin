package com.devone.bot.core.logic.task.playerlinked.protect;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.playerlinked.BotPlayerLinkedTask;


public class BotProtectTask extends BotPlayerLinkedTask {

    public BotProtectTask(Bot bot, Player player) {
        super(bot, player, "🛡️");
        setObjective("Protect");
    }

    @Override
    public void execute() {
        this.stop();
    }
    
    @Override
    public void stop() {
        isDone = true;
    }

}
