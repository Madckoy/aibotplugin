package com.devone.bot.core.brain.logic.navigator.math.filters;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

import java.util.ArrayList;
import java.util.List;

public class BotAddDummyBlock {

    public static List<BotBlockData> add(BotPosition botPosition, List<BotBlockData> blocks) {
        if (blocks == null) return List.of();

        List<BotBlockData> result = new ArrayList<>(blocks);

        BotPosition pos = new BotPosition(botPosition.getX(), botPosition.getY()-1, botPosition.getZ());
        BotBlockData dummy = new BotBlockData();
        dummy.setPosition(pos);
        dummy.setType("DUMMY");
        dummy.setTag("reachable:start");

        result.add(dummy);

        return result;
    }
}
