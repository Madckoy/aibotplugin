package com.devone.bot.core.brain.memory;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;

public class BotMemoryV2Utils {

    public static void incrementCounter(Bot bot, String key) {
        if (bot == null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition stats = memory.partition("stats", BotMemoryV2Partition.Type.MAP);
        stats.increment(key);
    }

    public static void incrementSummaryCounter(Bot bot, String summaryKey, String itemKey) {
        if (bot == null || itemKey == null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition stats = memory.partition("stats", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition summary = stats.partition(summaryKey, BotMemoryV2Partition.Type.MAP);

        summary.increment(itemKey);
        summary.increment("total");
    }
}
