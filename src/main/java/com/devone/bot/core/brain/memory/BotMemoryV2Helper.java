package com.devone.bot.core.brain.memory;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;

public class BotMemoryV2Helper {
    public void memorize(Bot bot, String partition, String key, Object value) {
        bot.getBrain().getMemoryV2().partition(partition).put(key, value);
    }

    public void increment(Bot bot, String partition, String key) {
        bot.getBrain().getMemoryV2().partition(partition).increment(key);
    }

    public void addWaypoint(Bot bot, BotPosition position) {
        bot.getBrain().getMemoryV2().partition("waypoints", BotMemoryV2Partition.Type.LIST)
                .addToList(position);
    }

    public String snapshot(Bot bot) {
        return bot.getBrain().getMemoryV2().toJson();
    }

    public BotMemoryV2Partition accessPartition(Bot bot, String partition) {
        return bot.getBrain().getMemoryV2().partition(partition);
    }
}
