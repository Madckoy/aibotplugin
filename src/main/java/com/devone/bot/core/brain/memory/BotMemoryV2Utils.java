package com.devone.bot.core.brain.memory;

import java.util.ArrayList;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotMemoryV2Utils {

    public static void incrementPartitionItem(Bot bot, String part, String itemKey) {
        if (bot == null || itemKey == null || part==null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition partition = memory.partition(BotMemoryPartition.PartitionKey.STATS.toString(), BotMemoryV2Partition.Type.MAP);
        partition.increment(itemKey);
    }

    public static void incrementPartitionItems(Bot bot, String part, String subPart, String itemKey, String totalKey) {
        if (bot == null || itemKey == null || part==null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition currentPart=null;
        BotMemoryV2Partition partition = memory.partition(BotMemoryPartition.PartitionKey.STATS.toString(), BotMemoryV2Partition.Type.MAP);
        currentPart = partition;
        
        if(subPart!=null) {
            BotMemoryV2Partition subPartition = partition.partition(subPart, BotMemoryV2Partition.Type.MAP);
            currentPart = subPartition;
        }

        currentPart.increment(itemKey);
        currentPart.increment(totalKey);
    }


    public static void memorizePosition(Bot bot, BotPosition pos) {
        if (bot == null || pos == null) return;
        String key = pos.toKey().toString();

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);
        visited.put(key, System.currentTimeMillis());
    }

    public static void memorizeScanRadius(Bot bot, int scanRange) {
        if (bot == null) return;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav     = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        nav.put(BotMemoryItem.ItemKey.SCAN_RADIUS.toString(), scanRange);
    }

    public static void memorizeValue(Bot bot, String partition, String key, Object value) {
        if (bot == null) return;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part    = memory.partition(partition, BotMemoryV2Partition.Type.MAP);
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
        BotMemoryV2Partition nav = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);

        return visited.get(key) != null;
    }

    public static int cleanupVisited(Bot bot, long ttlMillis) {
        if (bot == null) return 0;
        long now = System.currentTimeMillis();
        int removed = 0;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);

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
        BotMemoryV2Partition nav = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);
        visited.getMap().clear();
    }

}
