package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

public class BotOnSightFilter {

    public static List<BotBlockData> filter(
        List<BotBlockData> candidates,
        List<BotBlockData> viewSector
    ) {
        Set<BotPositionKey> viewKeys = viewSector.stream()
            .map(BotBlockData::toKey)
            .collect(Collectors.toSet());

        return candidates.stream()
            .filter(candidate -> viewKeys.contains(candidate.toKey()))
            .collect(Collectors.toList());
    }
}
