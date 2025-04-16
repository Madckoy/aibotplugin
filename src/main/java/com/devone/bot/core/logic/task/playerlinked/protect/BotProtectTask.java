package com.devone.bot.core.logic.task.playerlinked.protect;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.playerlinked.BotPlayerLinkedTask;
import com.devone.bot.core.logic.task.playerlinked.protect.params.BotProtectTaskParams;

public class BotProtectTask extends BotPlayerLinkedTask<BotProtectTaskParams> {

    public BotProtectTask(Bot bot, Player player) {
        super(bot, player);
        setParams(new BotProtectTaskParams()); // Загружаем параметры по умолчанию
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
