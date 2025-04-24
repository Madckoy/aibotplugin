package com.devone.bot.core.brain.logic.navigator.math.filters;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
    
import java.util.ArrayList;
import java.util.List;
    
public class BotAddStartNavigationFilter {
    
    public static List<BotBlockData> apply(BotPosition botPosition, List<BotBlockData> blocks) {

        List<BotBlockData> result = new ArrayList<>(blocks);

        if (blocks == null) return List.of();
    
        //boolean hasBase = blocks.stream().anyMatch(b ->
        //    b.getX() == botPosition.getX() &&
        //  b.getY() == botPosition.getY()-1 &&
        //    b.getZ() == botPosition.getZ()
        //);
    
        //if (!hasBase) {
            BotBlockData start = new BotBlockData();
            start.setX(botPosition.getX());
            start.setY(botPosition.getY()-1); // start from the legs
            start.setZ(botPosition.getZ());
            start.setType("FAKE_BLOCK");
            start.setNotes("navigator:start");

            result.add(start);
            //return result;
        //
    
        return result;
    }
    
}