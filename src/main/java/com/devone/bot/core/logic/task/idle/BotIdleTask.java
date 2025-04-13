package com.devone.bot.core.logic.task.idle;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.idle.config.BotIdleTaskConfig;
import com.devone.bot.core.logic.task.playerlinked.BotPlayerLinkedTask;


public class BotIdleTask extends BotPlayerLinkedTask {

    public BotIdleTask(Bot bot, Player player) {
        super(bot, player, "🍹");

        setObjective("Idle");
        
        config = new BotIdleTaskConfig();
        this.isLogged = config.isLogged();
    }

    @Override
    public void execute() {
        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;

    }

}
