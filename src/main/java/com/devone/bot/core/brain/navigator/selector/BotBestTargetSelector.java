package com.devone.bot.core.brain.navigator.selector;

import java.util.List;
import java.util.Random;

import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotBestTargetSelector {

    public static BotBlockData selectRandom(List<BotBlockData> candidates) {
        Random random = new Random();
        if (candidates == null || candidates.isEmpty())
            return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

}
