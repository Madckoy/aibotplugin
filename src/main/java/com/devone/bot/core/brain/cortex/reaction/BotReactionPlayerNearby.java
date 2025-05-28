package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequencePlayerNearby;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BotReactionPlayerNearby implements IBotReaction {

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 🙋🏻‍♂️ Проверка реакции на игрока");

        if (BotInventory.isEmpty(bot)) {
            BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 🙋🏻‍♂️ Инвентарь пуст — реакции не будет");
            return Optional.empty();
        }

        BotPosition botLoc = bot.getNavigator().getPosition();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline() || player.isDead())
                continue;

            BotPosition playerLoc = new BotPosition(BotWorldHelper.locationToBotPosition(player.getLocation()));
            double dist = botLoc.distanceTo(playerLoc);

            if (dist < BotConstants.DEFAULT_PLAYER_DETECTION_RADIUS) {
                BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 🙋🏻‍♂️ Обнаружен игрок " + player.getName() + " на "
                        + String.format("%.1f", dist) + " м");

                return Optional.of(() -> {
                    BotTaskManager.push(bot, new BotSequencePlayerNearby(bot, player));
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
