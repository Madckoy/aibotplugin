package com.devone.bot.core.brain.cortex;

import java.util.*;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotActionSelector {

    public static Optional<Runnable> selectWeightedRandom(Bot bot, List<BotTaskCandidate> candidates) {
        List<BotTaskCandidate> available = candidates.stream()
            .filter(BotTaskCandidate::isAvailable)
            .toList();

        BotLogger.debug("⚙️", bot.isLogged(), "🧠 Доступные кандидаты на действие: " + candidates.size());

        for (BotTaskCandidate c : candidates) {
                BotLogger.debug("⚙️", bot.isLogged(),  "Кандидат: weight=" + c.getWeight() + " available=" + c.isAvailable());
        }    
        
        if (available.isEmpty()) return Optional.empty();

        double totalWeight = available.stream().mapToDouble(BotTaskCandidate::getWeight).sum();

        if (totalWeight <= 0) return Optional.empty();

        double random = Math.random() * totalWeight;
        double cumulative = 0.0;

        for (BotTaskCandidate candidate : available) {
            cumulative += candidate.getWeight();
            if (random <= cumulative) {
                Runnable task = candidate.getTask();
                return Optional.ofNullable(task); // <--- здесь
            }
        }

        return Optional.empty();
    }
}
