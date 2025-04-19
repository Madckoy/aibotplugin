package com.devone.bot.core.bot.task.reactive.reaction;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.bot.task.reactive.sequence.BotReactiveSequenceTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 🧩 Параметризованная реакция — позволяет настраивать условие снаружи
 */
public class BotReactiveCustomReaction implements IBotReactionStrategy {

    private final String reactionName;
    private final double minHealth;
    private final double detectionRadius;

    public BotReactiveCustomReaction(String reactionName, double minHealth, double detectionRadius) {
        this.reactionName = reactionName;
        this.minHealth = minHealth;
        this.detectionRadius = detectionRadius;
    }

    @Override
    public Optional<Runnable> check(Bot bot) {

        BotLogger.debug("🤖", true, bot.getId() + " 🧪 Проверка параметризованной реакции: " + getName());

        // 🔒 Условие: здоровье + возможно, местоположение или враги в радиусе
        boolean lowHealth = bot.getState().getHealth() < minHealth;

        if (!lowHealth) return Optional.empty();

        if (BotReactiveUtils.isAlreadyReacting(bot)) {
            BotLogger.debug("🤖", true, bot.getId() + " 🔁 [Custom] Уже реагирует — пропускаем");
            return BotReactiveUtils.avoidOverReaction(bot);
        }

        BotReactiveUtils.activateReaction(bot);

        return Optional.of(() -> {
            BotLogger.debug("🤖", true, bot.getId() + " 🔥 [Custom] Реакция активирована: " + getName());

            List<BotTask<?>> tasks = new ArrayList<>();

            // Добавь задачи по своему сценарию
            // tasks.add(...);

            BotReactiveSequenceTask sequence = new BotReactiveSequenceTask(bot, tasks);
            BotUtils.pushTask(bot, sequence);
        });
    }

    @Override
    public String getName() {
        return "⚙️ " + reactionName + " (hp < " + minHealth + ", radius = " + detectionRadius + ")";
    }
}
