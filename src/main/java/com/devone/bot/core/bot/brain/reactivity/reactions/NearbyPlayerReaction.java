package com.devone.bot.core.bot.brain.reactivity.reactions;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.behaviour.task.BotTask;
import com.devone.bot.core.bot.behaviour.task.drop.BotDropAllTask;
import com.devone.bot.core.bot.behaviour.task.move.BotMoveTask;
import com.devone.bot.core.bot.behaviour.task.move.params.BotMoveTaskParams;
import com.devone.bot.core.bot.brain.reactivity.IBotReactionStrategy;
import com.devone.bot.core.bot.brain.reactivity.sequence.BotReactiveSequenceTask;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;
import com.devone.bot.core.bot.brain.logic.utils.world.BotWorldHelper;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NearbyPlayerReaction implements IBotReactionStrategy {

    private static final double DETECTION_RADIUS = 10.0;

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLocation botLoc = bot.getNavigation().getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline() || player.isDead()) continue;

            BotLocation playerLoc = new BotLocation(BotWorldHelper.worldLocationToBotLocation(player.getLocation()));
            double dist = botLoc.distanceTo(playerLoc);

            if (dist < DETECTION_RADIUS) {
                return Optional.of(() -> {
                    BotLogger.debug("🎒", true, bot.getId() + " Игрок рядом: " + player.getName() + ". Передаём дары.");

                    List<BotTask<?>> tasks = new ArrayList<>();

                    // 1. Подход к игроку
                    BotMoveTaskParams walkParams = new BotMoveTaskParams(playerLoc);
                    BotMoveTask walkTask = new BotMoveTask(bot);
                    walkTask.setParams(walkParams);
                    walkTask.setObjective("Идём к игроку");

                    // 2. Выброс всего
                    BotDropAllTask dropTask = new BotDropAllTask(bot, player);
                    dropTask.setObjective("Передаём ресурсы");

                    tasks.add(walkTask);
                    tasks.add(dropTask);

                    BotReactiveSequenceTask sequence = new BotReactiveSequenceTask(bot, tasks);
                    bot.getLifeCycle().getTaskStackManager().pushTask(sequence);
                });
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Игрок рядом — выдать ресурсы";
    }
}
