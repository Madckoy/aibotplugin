package com.devone.bot.core.brain.cortex.reaction.reactive;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.reaction.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceContainerFishInWater;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import org.bukkit.Material;

import java.util.*;

public class BotReactionReactiveFishInWater implements IBotReaction {

    private static final Material[] FISH_TYPES = {
        Material.COD,
        Material.SALMON,
        Material.TROPICAL_FISH,
        Material.PUFFERFISH
    };

    @Override
    public Optional<Runnable> validate(Bot bot) {
        String id = bot.getId();
        boolean isLogged = bot.isLogged();

        BotLogger.debug("🎣", isLogged, id + " 🎯 Проверка: бот стоит в воде?");

        if (!BotWorldHelper.isInFishableWater(bot)) {
            return Optional.empty();
        }

        BotInventory inv = bot.getInventory();

        BotLogger.debug("🎣", isLogged, id + " 🔍 Проверка: есть ли уже рыба в инвентаре?");
        boolean hasFish = Arrays.stream(FISH_TYPES).anyMatch(type -> inv.getAmount(type) > 0);
        if (hasFish) {
            BotLogger.debug("🎣", isLogged, id + " 🐟 Уже есть рыба — ловить не нужно.");
            return Optional.empty();
        }

        BotLogger.debug("🎣", isLogged, id + " 📦 Проверка: есть ли место в инвентаре?");
        Set<Material> fishSet = new HashSet<>(Arrays.asList(FISH_TYPES));
        if (!BotInventory.hasFreeInventorySpace(bot, fishSet)) {
            BotLogger.debug("🎣", isLogged, id + " ❌ Нет места для улова — не ловим.");
            return Optional.empty();
        }

        if (new Random().nextFloat() >= 0.5f) {
            BotLogger.debug("🎣", isLogged, id + " 🤷 Шанс не сработал (random > 0.5)");
            return Optional.empty();
        }

        BotLogger.debug("🎣", isLogged, id + " ✅ Условия выполнены — запускаем реакцию на рыбалку");

        return Optional.of(() -> BotTaskManager.push(bot, new BotSequenceContainerFishInWater(bot)));
    }

    @Override
    public String getName() {
        return "🎣 Вода под ногами — поймать рыбу";
    }
}
