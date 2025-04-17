package com.devone.bot.core.logic.navigation;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotBlockVisited {
    private long timestamp = System.currentTimeMillis();
    private BotBlockData block;

    public BotBlockVisited(BotBlockData block) {
        this.block = block;
    }

    public long getAge() {
        return timestamp;
    }

    public BotBlockData getBlock(){
        return block;
    }

}
