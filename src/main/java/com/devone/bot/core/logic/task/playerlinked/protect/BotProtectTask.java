package com.devone.bot.core.logic.task.playerlinked.protect;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.logic.task.playerlinked.protect.params.BotProtectTaskParams;


public class BotProtectTask extends BotPlayerLinkedTask {
    BotProtectTaskParams params = new BotProtectTaskParams();

    public BotProtectTask(Bot bot, Player player) {
        super(bot, player);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
    }

    @Override
    public void execute() {
        this.stop();
    }

}
