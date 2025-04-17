package com.devone.bot.core.logic.navigation;
import com.devone.bot.utils.blocks.BotLocation;

import com.devone.bot.utils.blocks.BotBlockData;

import java.util.HashMap;
import java.util.Map;

public class BotBlocksVisited {

    Map<BotLocation, BotBlockVisited> visited = new HashMap<BotLocation, BotBlockVisited>();

    public void addVisited(BotBlockData block) {
        BotBlockVisited bv = new BotBlockVisited(block);
        visited.put(block.getLocation(),bv);
    }

    public boolean isVisited(BotBlockData block){
        BotBlockVisited bv = visited.get(block.getLocation());
        if(bv!=null) {
            return true;
        }
        else {
            return false;
        }
    }

    public long invalidate() {
        long size = 0;
        long currTime = System.currentTimeMillis();
        for (BotBlockVisited block: visited.values()) {
            long itemAge = block.getAge();
            long diff = currTime - itemAge;
            
            if(diff>2000000) { // approx 30 minutes
                size++;
                visited.remove(block.getBlock().getLocation());
            }
            
        }
        return size;
    }

}
