package com.devone.bot.core.task.reactive;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveUtils {

    // 🧠 Активируем реакцию и запоминаем, кто её начал
    public static void activateReaction(Bot bot, boolean status) {
        bot.getBrain().setReactionInProgress(status);
        BotTask<?> activeTask = null;

        if (status) {
            try {
                activeTask = bot.getActiveTask();
                bot.getBrain().setCurrentReactionOwner(activeTask.getUUID());
                BotLogger.debug(bot.getActiveTask().getIcon(), bot.isLogged(), bot.getId() + "  Activate reaction for: "+activeTask.getClass().getSimpleName());
            } catch (Exception ex) {

            }
        } else {
            BotLogger.debug("*", bot.isLogged(), bot.getId() + "  DE-Activate current reaction");
            bot.getBrain().clearCurrentReactionOwner();
            bot.getBrain().setReactionInProgress(false);
        }
    }

    public static Optional<Runnable> avoidOverReaction(Bot bot) {
        return Optional.empty(); // можно вернуть log или wrap
    }

    public static boolean isAlreadyReacting(Bot bot) {
        return bot.getBrain().isReactionInProgress();
    }

    // ✅ Проверка: именно эта задача владеет реакцией?
    public static boolean isReactionOwnedBy(Bot bot, BotTask<?> task) {
        String ownerUUID = bot.getBrain().getCurrentReactionOwner();
        if(ownerUUID==null) return false;
        if(task.getUUID().contentEquals(ownerUUID)==true) { 
            return true; 
        } else { 
            return false;
        }
    }
}
