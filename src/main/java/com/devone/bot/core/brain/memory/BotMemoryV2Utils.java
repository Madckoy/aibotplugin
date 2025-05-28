package com.devone.bot.core.brain.memory;

import java.util.ArrayList;
import java.util.Map;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class BotMemoryV2Utils {

    public static void incrementPartitionItem(Bot bot, BotMemoryPartition.PartitionKey part, BotMemoryItem.ItemKey itemKey) {
        if (bot == null || itemKey == null || part==null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition partition = memory.partition(part.toString(), BotMemoryV2Partition.Type.MAP);
        partition.increment(itemKey.toString());
    }

    public static void incrementNestedItem(Bot bot, BotMemoryPartition.PartitionKey part, String subPart, BotMemoryItem.ItemKey itemKey) {
        if (bot == null || itemKey == null || part==null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition currentPart=null;
        BotMemoryV2Partition partition = memory.partition(part.toString(), BotMemoryV2Partition.Type.MAP);
        currentPart = partition;
        
        if(subPart!=null) {
            BotMemoryV2Partition subPartition = partition.partition(subPart, BotMemoryV2Partition.Type.MAP);
            currentPart = subPartition;
        }

        currentPart.increment(itemKey.toString());        
    }

    public static void incrementNestedTotal(Bot bot, BotMemoryPartition.PartitionKey partitionKey, BotMemoryPartition.PartitionKey subPartitionKey, String itemKey, BotMemoryItem.ItemKey totalKey) {
        if (bot == null || itemKey == null || partitionKey == null || subPartitionKey == null || totalKey == null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        // Верхний уровень, например "STATS"
        BotMemoryV2Partition root = memory.partition(partitionKey.toString(), BotMemoryV2Partition.Type.MAP);

        // Раздел статистики, например "kills"
        BotMemoryV2Partition category = root.partition(subPartitionKey.toString(), BotMemoryV2Partition.Type.MAP);

        // Увеличиваем общий total по этой категории
        category.increment(totalKey.toString());

        // Получаем партишен сущности, например "ENDERMAN"
        BotMemoryV2Partition entity = category.partition(itemKey.toString(), BotMemoryV2Partition.Type.MAP);

        // Увеличиваем счётчик по конкретной сущности
        entity.increment(BotMemoryItem.ItemKey.COUNT.toString());
    }

    public static void memorizePositionAndTime(Bot bot, BotPosition pos) {
        if (bot == null || pos == null) return;
        String key = pos.toPositionKey().toString();

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);
        visited.put(key, System.currentTimeMillis());
    }

    public static void memorizeScanRadius(Bot bot, int scanRange) {
        if (bot == null) return;
        writeValue(bot, BotMemoryPartition.PartitionKey.NAVIGATION, BotMemoryItem.ItemKey.SCAN_RADIUS, scanRange);
    }

    public static void writeValue(Bot bot, BotMemoryPartition.PartitionKey partition, BotMemoryItem.ItemKey key, Object value) {
        if (bot == null) return;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part    = memory.partition(partition.toString(), BotMemoryV2Partition.Type.MAP);
        part.put(key.toString(), value);
    }
 
    public static <T> void writeValueTyped(Bot bot, BotMemoryPartition.PartitionKey partition, BotMemoryItem.ItemKey key, T value) {
       if (bot == null) return;
        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part = memory.partition(partition.toString(), BotMemoryV2Partition.Type.MAP);

        part.put(key.toString(), value);
    }

    private static Object readValueUntyped(Bot bot, BotMemoryPartition.PartitionKey partition, BotMemoryItem.ItemKey key) {
        if (bot == null) return null;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part    = memory.partition(partition.toString(), BotMemoryV2Partition.Type.MAP);
        return part.get(key.toString());
    }

    public static void writeNestedValue(Bot bot, BotMemoryPartition.PartitionKey partition, BotMemoryPartition.PartitionKey subPartition, BotMemoryItem.ItemKey key, Object value) {
        if (bot == null) return;
        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition root = memory.partition(partition.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition sub = root.partition(subPartition.toString(), BotMemoryV2Partition.Type.MAP);

        sub.put(key.toString(), value);
    }

    public static <T> void writeNestedValueTyped(Bot bot, BotMemoryPartition.PartitionKey partition, BotMemoryPartition.PartitionKey subPartition, BotMemoryItem.ItemKey key, T value) {
        if (bot == null) return;
        
        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition root = memory.partition(partition.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition sub = root.partition(subPartition.toString(), BotMemoryV2Partition.Type.MAP);

        sub.put(key.toString(), value);
    }

    public static boolean isPositionVisited(Bot bot, BotPosition pos) {
        if (bot == null || pos == null) return false;
        String key = pos.toPositionKey().toString();

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

    public static <T> T readValueTyped(Bot bot, BotMemoryPartition.PartitionKey partition, BotMemoryItem.ItemKey key, Class<T> clazz) {
        if (bot == null || partition == null || key == null || clazz == null) return null;

        Object raw = readValueUntyped(bot, partition, key);
        if (raw == null) return null;

        if (clazz.isInstance(raw)) {
            return clazz.cast(raw);
        }

        if (raw instanceof Map) {
            try {
                JsonElement json = new Gson().toJsonTree(raw);
                return new Gson().fromJson(json, clazz);
            } catch (Exception e) {
                BotLogger.debug("🧠", true, "❌ Ошибка реконструкции " + clazz.getSimpleName() +
                    " из карты (key: " + key + "): " + e.getMessage());
                return null;
            }
        }

        BotLogger.debug("🧠", true, "❌ Невозможно привести " + key + " (" + raw.getClass().getSimpleName() + ") к " + clazz.getSimpleName());
        return null;
    }

    // for Blocks
    public static void clearAllVisited(Bot bot) {
        if (bot == null) return;
    
        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition nav = memory.partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);
        visited.getMap().clear();
    }

    public static boolean isBlockVisited(Bot bot, BotBlockData block) {
        if (bot == null || block == null) return false;

        String key = block.getX() + ", " + block.getY() + ", " + block.getZ();

        BotMemoryV2Partition visited = bot.getBrain()
            .getMemoryV2()
            .partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP)
            .partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);

        return visited.getMap().containsKey(key);
    }
}
