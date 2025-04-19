package com.devone.bot.core.bot.task.reactive.reaction;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.drop.BotDropAllTask;
import com.devone.bot.core.bot.task.active.move.BotMoveTask;
import com.devone.bot.core.bot.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.sequence.BotReactiveSequenceTask;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NearbyPlayerReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {

        BotLogger.debug("🤖", true, bot.getId() + " 🙋🏻‍♂️ Проверка реакции бота на игрока");

        BotLocation botLoc = bot.getNavigation().getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline() || player.isDead()) continue;

            BotLocation playerLoc = new BotLocation(BotWorldHelper.worldLocationToBotLocation(player.getLocation()));
            double dist = botLoc.distanceTo(playerLoc);

            if (dist < BotConstants.DEFAULT_DETECTION_RADIUS) {

                BotLogger.debug("🤖", true, bot.getId() + " 🙋🏻‍♂️ Обнаружен игрок " + player.getName() +
                        " на расстоянии " + String.format("%.1f", dist) + " м");

                if (BotReactiveUtils.isAlreadyReacting(bot)) {
                    BotLogger.debug("🤖", true, bot.getId() + " 🙋🏻‍♂️ Уже выполняется реакция — прерываем");
                    return BotReactiveUtils.avoidOverReaction(bot);
                }

                BotReactiveUtils.activateReaction(bot);

                return Optional.of(() -> {
                    BotLogger.debug("🤖", true, bot.getId() + " 🙌 Реакция: отдаём ресурсы игроку " + player.getName());

                    List<BotTask<?>> tasks = new ArrayList<>();

                    // 1. Подход к игроку
                    BotMoveTaskParams walkParams = new BotMoveTaskParams(playerLoc);
                    BotMoveTask walkTask = new BotMoveTask(bot);
                    walkTask.setParams(walkParams);
                    walkTask.setObjective("🥾 Идём к игроку");

                    // 2. Выброс всего
                    BotDropAllTask dropTask = new BotDropAllTask(bot, player);
                    dropTask.setObjective("📦 Передаём ресурсы");

                    tasks.add(walkTask);
                    tasks.add(dropTask);

                    BotReactiveSequenceTask sequence = new BotReactiveSequenceTask(bot, tasks);
                    BotUtils.pushTask(bot, sequence);
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
