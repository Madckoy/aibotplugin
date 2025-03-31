package com.devone.aibot.core.logic.tasks;

import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotIdleTaskConfig;


public class BotIdleTask extends BotPlayerLinkedTask {

    public BotIdleTask(Bot bot, Player player) {
        super(bot, player, "🍹");

        setObjective("Idle");
        
        config = new BotIdleTaskConfig();
        logging = config.isLogging();
    }

    @Override
    public void executeTask() {
    
    }
}
