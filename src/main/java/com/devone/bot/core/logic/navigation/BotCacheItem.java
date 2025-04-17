package com.devone.bot.core.logic.navigation;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotCacheItem {
    private long timestamp = System.currentTimeMillis();
    private BotBlockData block;

    public BotCacheItem(BotBlockData block) {
        this.block = block;
    }

    public long getAge() {
        return timestamp;
    }

    public BotBlockData getBlock(){
        return block;
    }

}
