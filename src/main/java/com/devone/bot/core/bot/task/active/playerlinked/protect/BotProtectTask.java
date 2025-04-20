package com.devone.bot.core.bot.task.active.playerlinked.protect;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.bot.task.active.playerlinked.protect.params.BotProtectTaskParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;

public class BotProtectTask extends BotPlayerLinkedTask<BotProtectTaskParams> {

    public BotProtectTask(Bot bot, Player player) {
        super(bot, player, BotProtectTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotProtectTaskParams> setParams(BotProtectTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        // Реализация логики защиты — пока заглушка
        this.stop();
    }
}
