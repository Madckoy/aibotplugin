package com.devone.bot.core.brain.logic.navigator.math.filters;

import java.util.List;
import java.util.stream.Collectors;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotSightFilter {

    public static List<BotBlockData> filter(
        List<BotBlockData> candidates,
        List<BotBlockData> viewSector
    ) {
        final double epsilon = 1; // радиус допуска

        return candidates.stream()
            .filter(candidate -> viewSector.stream().anyMatch(sectorBlock ->
                candidate.getPosition().distanceTo(sectorBlock.getPosition()) <= epsilon
            ))
            .collect(Collectors.toList());
    }
}
