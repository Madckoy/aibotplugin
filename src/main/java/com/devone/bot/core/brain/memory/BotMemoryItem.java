package com.devone.bot.core.brain.memory;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotMemoryItem {
    private long timestamp = System.currentTimeMillis();
    private BotBlockData block;

    public BotMemoryItem(BotBlockData block) {
        this.block = block;
    }

    public long getAge() {
        return timestamp;
    }

    public BotBlockData getBlock(){
        return block;
    }

}
