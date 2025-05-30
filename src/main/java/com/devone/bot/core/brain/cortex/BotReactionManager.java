package com.devone.bot.core.brain.cortex;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.reaction.BotReactionInWater;
import com.devone.bot.core.brain.cortex.reaction.BotReactionMoveTo;
import com.devone.bot.core.brain.cortex.reaction.BotReactionNavigationSimulate;
import com.devone.bot.core.brain.cortex.reaction.BotReactionNavigationСalculate;
import com.devone.bot.core.brain.cortex.reaction.BotReactionEnsureSpawned;
import com.devone.bot.core.brain.cortex.reaction.BotReactionEntityNearby;
import com.devone.bot.core.brain.cortex.reaction.BotReactionPlayerNearby;
import com.devone.bot.core.brain.cortex.reaction.BotReactionRotateToBestAngle;
import com.devone.bot.core.brain.cortex.reaction.BotReactionObstacleNearby;
import com.devone.bot.core.brain.cortex.reaction.BotReactionStuckGuard;
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
        // 📚 Регистрация на проблемы с Ентити
        register(new BotReactionEnsureSpawned());
        // 📚 Регистрация позиционных реакций
        register(new BotReactionStuckGuard());
        register(new BotReactionObstacleNearby());
        register(new BotReactionEntityNearby());
        register(new BotReactionPlayerNearby());
        register(new BotReactionInWater());
        // 📚 Регистрация навигационных реакций
        register(new BotReactionNavigationSimulate());
        register(new BotReactionNavigationСalculate());
        register(new BotReactionRotateToBestAngle());
        register(new BotReactionMoveTo());
 

        
        BotLogger.debug("🧠", AIBotPlugin.getInstance().isLogged(), "🧩 Зарегистрированы реакции: " + reactions.size());
    }

    public static List<BotReactionResult> checkReactions(Bot bot) {
        List<BotReactionResult> results = new ArrayList<>();

        for (IBotReaction reaction : reactions) {
            BotLogger.debug("🧠", bot.isLogged(), bot.getId() + " 🔍 Проверка реакции: " + reaction.getName());

            Optional<Runnable> action = reaction.validate(bot);

            if (action.isPresent()) {
                BotLogger.debug("🧠", bot.isLogged(), bot.getId() + " ✅ Реакция сработала: " + reaction.getName());
                results.add(new BotReactionResult(reaction.getName(), action.get()));

                if (reaction.shouldInterrupt(bot)) {
                    BotLogger.debug("🧠", bot.isLogged(), bot.getId() + " ⛔ Прерывание цепочки реакций: " + reaction.getName());
                    break;
                }
            }
        }

        if (results.isEmpty()) {
            BotLogger.debug("🧠", bot.isLogged(), bot.getId() + " ❌ Ни одна реакция не сработала");
        }

        return results;
    }

    public static void register(IBotReaction r) {
        reactions.add(r);
    }
}
