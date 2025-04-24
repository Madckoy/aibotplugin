package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.drop.BotDropAllTask;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveNearbyPlayerContainerParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.entity.Player;

public class BotReactiveNearbyPlayerContainer extends BotReactiveTaskContainer<BotReactiveNearbyPlayerContainerParams> {

    Player player = null;

    public BotReactiveNearbyPlayerContainer(Bot bot, Player player) {
        super(bot, BotReactiveNearbyPlayerContainerParams.class);
        this.player = player;
        setObjective("Reactive: BotMoveTask + BotDropAllTask");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());

        BotPosition playerLoc = new BotPosition(BotWorldHelper.locationToBotPosition(player.getLocation()));

        // 1. Идём к игроку
        BotMoveTaskParams walkParams = new BotMoveTaskParams(playerLoc);
        BotMoveTask walkTask = new BotMoveTask(bot);
        walkTask.setParams(walkParams);
        walkTask.setObjective("🥾 Идём к игроку");
        add(walkTask);

        // 2. Дропаем ресы
        BotDropAllTask dropTask = new BotDropAllTask(bot, player);
        dropTask.setObjective("🎁 Передаём ресурсы");
        add(dropTask);
    }
}
