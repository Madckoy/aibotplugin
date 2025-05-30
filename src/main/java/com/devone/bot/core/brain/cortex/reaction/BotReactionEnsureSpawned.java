package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.Location;

import java.util.Optional;

public class BotReactionEnsureSpawned implements IBotReaction {

    private static final String ICON = "🪄";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNPC().isSpawned()) return Optional.empty(); // всё в порядке

        BotLogger.warn(ICON, bot.isLogged(), bot.getId() + " ⚠️ NPC не заспавнен! Реакция восстановления.");

        BotPosition fallback = bot.getNavigator().getPosition();
        Location loc = BotWorldHelper.botPositionToWorldLocation(fallback);

        if (fallback != null && loc.getWorld() != null) {
            return Optional.of(() -> {
                bot.getNPC().spawn(loc);
                BotLogger.info(ICON, bot.isLogged(), bot.getId() + " ✅ Успешно заспавнен в " + fallback.toPositionKey());
            });
        }

        BotLogger.error(ICON, bot.isLogged(), bot.getId() + " ❌ Не удалось получить позицию навигатора. Реакция не выполнена.");
        return Optional.empty();
    }

    @Override
    public String getName() {
         return " Spawn bot's NPC entity";
    }
    
    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true;
    }
}
