package com.devone.bot.core.brain.logic.navigator.math.filters;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
    
import java.util.ArrayList;
import java.util.List;
    
public class BotAddDummyBlock {
    
    public static List<BotBlockData> apply(BotPosition botPosition, List<BotBlockData> blocks) {

        List<BotBlockData> result = new ArrayList<>(blocks);

        if (blocks == null) return List.of();
 
        BotBlockData start = new BotBlockData();
        start.setX(botPosition.getX());
        start.setY(botPosition.getY()-1);
        start.setZ(botPosition.getZ());
        start.setType("DUMMY");
        start.setTag("poi:start");

        result.add(start);
    
        return result;
    }
    
}