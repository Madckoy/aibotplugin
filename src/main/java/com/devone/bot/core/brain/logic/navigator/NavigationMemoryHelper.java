package com.devone.bot.core.brain.logic.navigator;

import java.util.ArrayList;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.utils.blocks.BotPosition;

public class NavigationMemoryHelper {

    public static void memorizePosition(Bot bot, BotPosition pos) {
        if (bot == null || pos == null) return;
        String key = pos.toKey().toString();

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition("navigation", BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition("visited", BotMemoryV2Partition.Type.MAP);

        visited.put(key, System.currentTimeMillis());
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
