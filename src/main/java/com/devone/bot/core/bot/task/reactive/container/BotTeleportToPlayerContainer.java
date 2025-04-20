package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.entity.Player;

public class BotTeleportToPlayerContainer extends BotTeleportToLocationContainer {

    public BotTeleportToPlayerContainer(Bot bot, Player player) {
        super(bot, BotWorldHelper.worldLocationToBotLocation(player.getLocation()));
        setObjective("Контейнер для BotTeleportTask в точку игрока" + player.getName());
    }
}
