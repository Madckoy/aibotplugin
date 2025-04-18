package com.devone.bot.core.bot.brain.memory;

import com.devone.bot.core.bot.utils.blocks.BotBlockData;

public class BotMemoryItem {
    private long timestamp = System.currentTimeMillis();
    private BotBlockData block;
    private String label;

    public BotMemoryItem(BotBlockData block) {
        this.block = block;
        this.label = generateLabel(block);
    }

    private String generateLabel(BotBlockData block) {
        // Генерация метки в зависимости от типа блока
        if (block.isAir()) {
            return "Пустой блок";
        } else if (block.isDangerous()) {
            return "Опасный блок";
        }
        // Можно добавлять другие условия в зависимости от типа блока
        return "Обычный блок";
    }

    public boolean isExpired(long expirationMillis, long currentTime) {
        return (currentTime - timestamp) > expirationMillis;
    }
    public BotBlockData getBlock() {
        return block;
    }

    public String getLabel() {
        return label;
    }

    public long getAge() {
        return timestamp;
    }
}
