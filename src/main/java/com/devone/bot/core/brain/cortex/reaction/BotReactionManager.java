package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.reaction.reactive.BotReactionReactiveFishInWater;
import com.devone.bot.core.brain.cortex.reaction.reactive.BotReactionReactiveNearbyHostile;
import com.devone.bot.core.brain.cortex.reaction.reactive.BotReactionReactiveNearbyPlayer;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 🧠 Менеджер реактивного поведения бота.
 * Проверяет, нужно ли выполнить реакцию на условия.
 */
public class BotReactionManager {

    private static final List<IBotReaction> reactions = new ArrayList<>();

    static {
        // 📚 Регистрация стандартных реакций
        registerIntention(new BotReactionReactiveNearbyHostile());
        registerIntention(new BotReactionReactiveNearbyPlayer());
        registerIntention(new BotReactionReactiveFishInWater()); // 🎣 ловим рыбу!

        BotLogger.debug("🧠", AIBotPlugin.getInstance().isLogged(), "🧩 Зарегистрированы предустановленные реакции: " + reactions.size());
    }

    public static Optional<Runnable> checkReactions(Bot bot) {

        BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(), bot.getId() + " 🧩 Проверка реакций...");

        for (IBotReaction reaction : reactions) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                    bot.getId() + " 🔎 Пробуем реакцию: " + reaction.getName());

            Optional<Runnable> option = reaction.validate(bot);

            if (option.isPresent()) {
                BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                        bot.getId() + " ✅ Реакция сработала: " + reaction.getName());
                return option;
            }
        }

        BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(), bot.getId() + " ❌ Ни одна реакция не активировалась");
        return Optional.empty();
    }

    public static void registerIntention(IBotReaction r) {
        reactions.add(r);
    }
}
