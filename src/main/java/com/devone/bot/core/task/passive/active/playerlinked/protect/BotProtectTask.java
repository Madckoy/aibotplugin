package com.devone.bot.core.task.passive.active.playerlinked.protect;

import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.passive.active.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.task.passive.active.playerlinked.protect.params.BotProtectTaskParams;

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
