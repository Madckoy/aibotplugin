package com.devone.bot.core.brain.memory;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.blocks.BotBlockData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BotMemory {

    Map<BotLocation, BotMemoryItem> visitedPlaces = new HashMap<BotLocation, BotMemoryItem>();

    public BotMemory(){
        super();
    }

    public void memorize(BotBlockData block) {
        BotMemoryItem bv = new BotMemoryItem(block);
        visitedPlaces.put(block.getLocation(),bv);
    }

    public boolean isMemorized(BotBlockData block){
        BotMemoryItem bv = visitedPlaces.get(block.getLocation());
        if(bv!=null) {
            return true;
        }
        else {
            return false;
        }
    }

    public long cleanup() {
        long removed = 0;
        long currTime = System.currentTimeMillis();
    
        Iterator<Map.Entry<BotLocation, BotMemoryItem>> it = visitedPlaces.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BotLocation, BotMemoryItem> entry = it.next();
            long age = currTime - entry.getValue().getAge();
    
            if (age > 30 * 60 * 1000) { // 30 минут
                it.remove(); // ✅ безопасное удаление
                removed++;
            }
        }
    
        return removed;
    }

}
