package com.devone.bot.core.brain.memory;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;

public class BotMemoryNestedUtils {

    /**
     * Запись значения в глубоко вложенный партишн.
     */
    @SafeVarargs
    public static <T> void writeNestedValueTyped(
            Bot bot,
            T value,
            BotMemoryItem.ItemKey key,
            BotMemoryPartition.PartitionKey... path
    ) {
        if (bot == null || key == null || value == null || path == null || path.length == 0) return;

        BotMemoryV2Partition current = bot.getBrain().getMemoryV2()
                .partition(path[0].toString(), BotMemoryV2Partition.Type.MAP);

        for (int i = 1; i < path.length; i++) {
            current = current.partition(path[i].toString(), BotMemoryV2Partition.Type.MAP);
        }

        current.put(key.toString(), value);
    }

    /**
     * Чтение значения из вложенного партишна.
     */
    @SafeVarargs
    public static <T> T readNestedValueTyped(
            Bot bot,
            BotMemoryItem.ItemKey key,
            Class<T> clazz,
            BotMemoryPartition.PartitionKey... path
    ) {
        if (bot == null || key == null || clazz == null || path == null || path.length == 0) return null;

        BotMemoryV2Partition current = bot.getBrain().getMemoryV2()
                .partition(path[0].toString());

        for (int i = 1; i < path.length; i++) {
            if (current == null) return null;
            current = current.partition(path[i].toString(), BotMemoryV2Partition.Type.MAP);
        }

        if (current == null) return null;

        Object raw = current.get(key.toString());
        return BotMemoryV2Utils.deserializeTyped(raw, clazz);
    }

    /**
     * Удаление значения по ключу из вложенного партишна.
     */
    @SafeVarargs
    public static void removeNestedValue(
            Bot bot,
            BotMemoryItem.ItemKey key,
            BotMemoryPartition.PartitionKey... path
    ) {
        if (bot == null || key == null || path == null || path.length == 0) return;

        BotMemoryV2Partition current = bot.getBrain().getMemoryV2()
                .partition(path[0].toString());

        for (int i = 1; i < path.length; i++) {
            if (current == null) return;
            current = current.partition(path[i].toString(), BotMemoryV2Partition.Type.MAP);
        }

        if (current != null) {
            current.remove(key.toString());
        }
    }
}
