package com.devone.bot.core.brain.behaviour;

import java.util.*;

public class BotBehaviorSelector {

    public static Optional<Runnable> selectWeightedRandom(List<BotTaskCandidate> candidates) {
        List<BotTaskCandidate> available = candidates.stream()
            .filter(BotTaskCandidate::isAvailable)
            .toList();

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
