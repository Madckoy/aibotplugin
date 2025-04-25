package com.devone.bot.core.task.reactive.container;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveTeleportToPlayerContainerParams;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotReactiveTeleportToPlayerContainer
        extends BotReactiveTaskContainer<BotReactiveTeleportToPlayerContainerParams> {

    private final Player player;

    public BotReactiveTeleportToPlayerContainer(Bot bot, Player player) {
        super(bot, BotReactiveTeleportToPlayerContainerParams.class);
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
        params.setPosition(new BotPosition(
                playerLoc.getBlockX(),
                playerLoc.getBlockY(),
                playerLoc.getBlockZ()));

        tp.setParams(params);
        tp.setIcon("⚡");
        tp.setObjective("Телепорт к игроку сзади");

        add(tp); // was add(tp) 📦 добавляем в контейнер
    }
}
