package com.devone.bot.core.task.reactive.strategy;

import com.devone.bot.core.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.task.reactive.container.BotNearbyPlayerReactiveContainer;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BotNearbyPlayerStrategy implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotLogger.debug("🤖", true, bot.getId() + " 🙋🏻‍♂️ Проверка реакции на игрока");

        if (BotInventory.isEmpty(bot)) {
            BotLogger.debug("🤖", true, bot.getId() + " 🙋🏻‍♂️ Инвентарь пуст — реакции не будет");
            return Optional.empty();
        }

        BotLocation botLoc = bot.getNavigation().getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline() || player.isDead())
                continue;

            BotLocation playerLoc = new BotLocation(BotWorldHelper.worldLocationToBotLocation(player.getLocation()));
            double dist = botLoc.distanceTo(playerLoc);

            if (dist < BotConstants.DEFAULT_DETECTION_RADIUS) {
                BotLogger.debug("🤖", true, bot.getId() + " 🙋🏻‍♂️ Обнаружен игрок " + player.getName() + " на "
                        + String.format("%.1f", dist) + " м");

                return Optional.of(() -> {
                    BotTaskManager.push(bot, new BotNearbyPlayerReactiveContainer(bot, player));
                });
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "🙋🏻‍♂️ Игрок рядом — выдать ресурсы";
    }
}
