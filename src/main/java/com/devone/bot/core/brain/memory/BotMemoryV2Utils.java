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

    public static void incrementPartitionItem(Bot bot, String part, String itemKey) {
        if (bot == null || itemKey == null || part==null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        BotMemoryV2Partition partition = memory.partition(BotMemoryPartition.PartitionKey.STATS.toString(), BotMemoryV2Partition.Type.MAP);
        partition.increment(itemKey);
    }

    public static void incrementNestedItem(Bot bot, String part, String subPart, String itemKey) {
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
    }

    public static void incrementNestedTotal(Bot bot, String partitionKey, String subPartitionKey, String itemKey, String totalKey) {
        if (bot == null || itemKey == null || partitionKey == null || subPartitionKey == null || totalKey == null) return;

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory == null) return;

        // Верхний уровень, например "STATS"
        BotMemoryV2Partition root = memory.partition(partitionKey, BotMemoryV2Partition.Type.MAP);

        // Раздел статистики, например "kills"
        BotMemoryV2Partition category = root.partition(subPartitionKey, BotMemoryV2Partition.Type.MAP);

        // Увеличиваем общий total по этой категории
        category.increment(totalKey);

        // Получаем партишен сущности, например "ENDERMAN"
        BotMemoryV2Partition entity = category.partition(itemKey, BotMemoryV2Partition.Type.MAP);

        // Увеличиваем счётчик по конкретной сущности
        entity.increment(BotMemoryItem.ItemKey.COUNT.toString());
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

    private static Object readMemoryValueUntyped(Bot bot, String partition, String key) {
        if (bot == null) return null;

        BotMemoryV2 memory           = bot.getBrain().getMemoryV2();
        BotMemoryV2Partition part    = memory.partition(partition, BotMemoryV2Partition.Type.MAP);
        return part.get(key);
    }

    public static <T> T readMemoryValueTyped(Bot bot, String partition, String key, Class<T> clazz) {
        if (bot == null || partition == null || key == null || clazz == null) return null;

        Object raw = readMemoryValueUntyped(bot, partition, key);
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
