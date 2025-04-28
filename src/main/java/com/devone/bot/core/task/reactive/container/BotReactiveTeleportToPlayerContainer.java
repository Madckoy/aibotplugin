package com.devone.bot.core.task.reactive.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveTeleportToPlayerContainerParams;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotReactiveTeleportToPlayerContainer
        extends BotReactiveContainer<BotReactiveTeleportToPlayerContainerParams> {

    private final Player player;

    public BotReactiveTeleportToPlayerContainer(Bot bot, Player player) {
        super(bot, BotReactiveTeleportToPlayerContainerParams.class);
        this.player = player;
        setIcon("🔀");
        setObjective("Reactive: Телепорт к игроку (не вплотную)");
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
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

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tp);
        return subtasks;
    }
}
