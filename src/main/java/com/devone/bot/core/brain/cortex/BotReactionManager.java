package com.devone.bot.core.brain.cortex;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.reaction.BotReactionInWater;
import com.devone.bot.core.brain.cortex.reaction.BotReactionMoveTo;
import com.devone.bot.core.brain.cortex.reaction.BotReactionEntityNearby;
import com.devone.bot.core.brain.cortex.reaction.BotReactionPlayerNearby;
import com.devone.bot.core.brain.cortex.reaction.BotReactionRotateToBestAngle;
import com.devone.bot.core.brain.cortex.reaction.BotReactionObstacleNearby;
import com.devone.bot.core.brain.cortex.reaction.BotReactionStuckGuard;
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

        register(new BotReactionStuckGuard());
        register(new BotReactionObstacleNearby());
        register(new BotReactionEntityNearby());
        register(new BotReactionPlayerNearby());
        register(new BotReactionInWater()); // 🎣 ловим рыбу!
        register(new BotReactionRotateToBestAngle());
        register(new BotReactionMoveTo());
        
        BotLogger.debug("🧠", AIBotPlugin.getInstance().isLogged(), "🧩 Зарегистрированы реакции: " + reactions.size());
    }

    public static Optional<BotReactionResult> checkReactions(Bot bot) {
        for (IBotReaction reaction : reactions) {
            Optional<Runnable> action = reaction.validate(bot);
            if (action.isPresent()) {
                BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                    bot.getId() + " 🔎 Пробуем реакцию: " + reaction.getName());            

                Optional<Runnable> option = reaction.validate(bot);                    
                
                if (option.isPresent()) {
                   BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(),
                        bot.getId() + " ✅ Реакция сработала: " + reaction.getName());

                return Optional.of(new BotReactionResult(reaction.getName(), action.get()));
                }                
            }
        }
        BotLogger.debug(BotUtils.getActiveTaskIcon(bot), bot.isLogged(), bot.getId() + " ❌ Ни одна реакция не активировалась");

        return Optional.empty(); // <- правильно
    }

    public static void register(IBotReaction r) {
        reactions.add(r);
    }
}
