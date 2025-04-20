package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.drop.BotDropAllTask;
import com.devone.bot.core.bot.task.active.move.BotMoveTask;
import com.devone.bot.core.bot.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotNearbyPlayerReactionContainerParams;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.entity.Player;

public class BotNearbyPlayerReactionContainer extends BotReactiveTaskContainer<BotNearbyPlayerReactionContainerParams> {

    Player player= null;

    public BotNearbyPlayerReactionContainer(Bot bot, Player player) {
        super(bot, BotNearbyPlayerReactionContainerParams.class);
        this.player = player;
        setIcon("🎁");
        setObjective("Передача лута игроку");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLocation playerLoc = new BotLocation(BotWorldHelper.worldLocationToBotLocation(player.getLocation()));

        BotLogger.debug(getIcon(), true, bot.getId() + " 🎁 Подходим к игроку " + player.getName() + " и передаём лут");

        // 1. Идём к игроку
        BotMoveTaskParams walkParams = new BotMoveTaskParams(playerLoc);
        BotMoveTask walkTask = new BotMoveTask(bot);
        walkTask.setParams(walkParams);
        walkTask.setObjective("🥾 Идём к игроку");
        bot.reactiveTaskStart(walkTask);

        // 2. Дропаем ресы
        BotDropAllTask dropTask = new BotDropAllTask(bot, player);
        dropTask.setObjective("📦 Передаём ресурсы");
        bot.reactiveTaskStart(dropTask);
    }
}
