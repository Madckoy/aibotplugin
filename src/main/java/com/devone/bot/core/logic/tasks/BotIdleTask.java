package com.devone.bot.core.logic.tasks;

import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotIdleTaskConfig;


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
