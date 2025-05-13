package com.devone.bot.core.brain.memory;

import java.util.ArrayList;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.utils.blocks.BotPosition;

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

    public static void memorizePosition(Bot bot, BotPosition pos) {
        if (bot == null || pos == null) return;
        String key = pos.toKey().toString();

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition("visited", BotMemoryV2Partition.Type.MAP);

        visited.put(key, System.currentTimeMillis());
    }

    public static void memorizeScanRange(Bot bot, int scanRange) {
        if (bot == null) return;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav     = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        nav.put("scan-range", scanRange);
    }

    public static void memorizeValue(Bot bot, String partition, String key, Object value) {
        if (bot == null) return;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part     = memory.partition(partition, BotMemoryV2Partition.Type.MAP);
        part.put(key, value);
    }

    public static Object readMemoryValue(Bot bot, String partition, String key) {
        if (bot == null) return null;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part    = memory.partition(partition, BotMemoryV2Partition.Type.MAP);
        return part.get(key);
    }

    public static boolean isPositionVisited(Bot bot, BotPosition pos) {
        if (bot == null || pos == null) return false;
        String key = pos.toKey().toString();

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition("visited", BotMemoryV2Partition.Type.MAP);

        return visited.get(key) != null;
    }

    public static int cleanupVisited(Bot bot, long ttlMillis) {
        if (bot == null) return 0;
        long now = System.currentTimeMillis();
        int removed = 0;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition("visited", BotMemoryV2Partition.Type.MAP);

        for (String key : new ArrayList<>(visited.getMap().keySet())) {
            Object value = visited.get(key);
            if (value instanceof Number ts && now - ts.longValue() > ttlMillis) {
                visited.remove(key);
                removed++;
            }
        }

        return removed;
    }

    public static void clearAllVisited(Bot bot) {
        if (bot == null) return;
    
        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition("visited", BotMemoryV2Partition.Type.MAP);
        visited.getMap().clear();
    }

}
