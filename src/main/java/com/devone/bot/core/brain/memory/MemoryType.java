package com.devone.bot.core.brain.memory;

public enum MemoryType {
    VISITED_BLOCKS("Visited "),
    SPOTTED_SPECIAL_BLOCK("Special Blocks"),
    SPOTTED_HOSTILE_ENCOUNTER("Hostile mobs"),
    ITEM_DROP("Dropped Items"),
    SPOTTED_FRIENDLY_ENTITY("Friendly Mobs"),
    BLOCKS_BREAK_SUMMARY("Broken blocks"), 
    ENTITIES_KILLED_SUMMARY("Killed Entities"); 

    private final String label;

    MemoryType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
