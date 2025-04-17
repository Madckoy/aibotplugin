package com.devone.bot.core.logic.navigation;
import com.devone.bot.utils.blocks.BotLocation;

import com.devone.bot.utils.blocks.BotBlockData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BotCache {

    Map<BotLocation, BotCacheItem> cached = new HashMap<BotLocation, BotCacheItem>();

    public BotCache(){
        super();
    }

    public void add(BotBlockData block) {
        BotCacheItem bv = new BotCacheItem(block);
        cached.put(block.getLocation(),bv);
    }

    public boolean isCached(BotBlockData block){
        BotCacheItem bv = cached.get(block.getLocation());
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
    
        Iterator<Map.Entry<BotLocation, BotCacheItem>> it = cached.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BotLocation, BotCacheItem> entry = it.next();
            long age = currTime - entry.getValue().getAge();
    
            if (age > 30 * 60 * 1000) { // 30 минут
                it.remove(); // ✅ безопасное удаление
                removed++;
            }
        }
    
        return removed;
    }

}
