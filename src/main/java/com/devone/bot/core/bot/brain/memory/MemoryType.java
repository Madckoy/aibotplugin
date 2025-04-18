package com.devone.bot.core.bot.brain.memory;

public enum MemoryType {
    VISITED("Посещённые места"),
    SPECIAL_BLOCK("Особые блоки"),
    HOSTILE_ENCOUNTER("Враждебные мобы"),
    ITEM_DROP("Сброшенные предметы"),
    FRIENDLY_ENTITY("Дружелюбные мобы");

    private final String label;

    MemoryType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
