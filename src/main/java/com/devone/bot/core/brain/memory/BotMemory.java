package com.devone.bot.core.brain.memory;

import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.blocks.BotBlockData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.EnumMap;

public class BotMemory {

    // Карта для хранения типов памяти, каждый тип имеет свой набор посещённых мест
    private final Map<MemoryType, Map<BotLocation, BotMemoryItem>> memoryMap;

    public BotMemory() {
        memoryMap = new EnumMap<>(MemoryType.class); // Используем EnumMap для лучшей производительности при работе с перечислениями
        for (MemoryType type : MemoryType.values()) {
            memoryMap.put(type, new HashMap<>());
        }
    }

    // Запоминаем блок для конкретного типа памяти
    public void memorize(BotBlockData block, MemoryType memoryType) {
        BotMemoryItem item = new BotMemoryItem(block);
        memoryMap.get(memoryType).put(block.getLocation(), item);
    }

    // Проверка, был ли блок запомнен для определённого типа памяти
    public boolean isMemorized(BotBlockData block, MemoryType memoryType) {
        return memoryMap.get(memoryType).containsKey(block.getLocation());
    }

    // Очистка устаревших записей по всем типам памяти
    public long cleanup(long expirationMillis) {
        long removed = 0;
        long currentTime = System.currentTimeMillis();

        // Перебираем все типы памяти
        for (Map<BotLocation, BotMemoryItem> memory : memoryMap.values()) {
            Iterator<Map.Entry<BotLocation, BotMemoryItem>> it = memory.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BotLocation, BotMemoryItem> entry = it.next();
                BotMemoryItem item = entry.getValue();
                
                // Если элемент устарел, удаляем его
                if (item.isExpired(expirationMillis, currentTime)) {
                    it.remove();
                    removed++;
                }
            }
        }

        return removed;
    }
    
    // Получить все данные для конкретного типа памяти
    public Map<BotLocation, BotMemoryItem> getMemoryForType(MemoryType memoryType) {
        return memoryMap.get(memoryType);
    }
}
