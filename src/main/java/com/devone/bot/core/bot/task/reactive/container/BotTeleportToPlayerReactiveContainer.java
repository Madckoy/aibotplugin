package com.devone.bot.core.bot.task.reactive.container;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;

import com.devone.bot.core.bot.task.reactive.container.params.BotTeleportToPlayerReactiveContainerParams;
import com.devone.bot.core.utils.blocks.BotLocation;

public class BotTeleportToPlayerReactiveContainer
        extends BotReactiveTaskContainer<BotTeleportToPlayerReactiveContainerParams> {

    private final Player player;

    public BotTeleportToPlayerReactiveContainer(Bot bot, Player player) {
        super(bot, BotTeleportToPlayerReactiveContainerParams.class);
        this.player = player;

        setIcon("📍");
        setObjective("Телепорт к игроку (не вплотную)");
    }

    @Override
    protected void enqueue(Bot bot) {
        Location playerLoc = player.getLocation();

        // 📏 Смещаемся на 2 блока назад по направлению взгляда
        // Vector offset = playerLoc.getDirection().normalize().multiply(-2);
        // Location behind = playerLoc.clone().add(offset);

        BotTeleportTask tp = new BotTeleportTask(bot, player);
        BotTeleportTaskParams params = new BotTeleportTaskParams();
        params.setLocation(new BotLocation(
                playerLoc.getBlockX(),
                playerLoc.getBlockY(),
                playerLoc.getBlockZ()));

        tp.setParams(params);
        tp.setIcon("⚡");
        tp.setObjective("Телепорт к игроку сзади");

        add(tp); // 📦 добавляем в контейнер
    }
}
