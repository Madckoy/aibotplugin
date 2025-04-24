package com.devone.bot.core.task.reactive;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTask;

public class BotReactiveUtils {

    // 🧠 Активируем реакцию и запоминаем, кто её начал
    public static void activateReaction(Bot bot, boolean status) {
        bot.getBrain().setReactionInProgress(status);

        if (status) {
            BotTask<?> activeTask = bot.getActiveTask();
            if (activeTask != null) {
                bot.getBrain().setCurrentReactionOwner(activeTask.getUUID());
            }
        } else {
            bot.getBrain().clearCurrentReactionOwner();
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
        return ownerUUID != null && ownerUUID.equals(task.getUUID());
    }
}
