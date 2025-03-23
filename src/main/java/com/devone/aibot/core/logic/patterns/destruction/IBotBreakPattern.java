package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;


import java.util.Map;


public interface IBotBreakPattern {
    Location findNextBlock(Bot bot, Map<Location, ?> geoMap);
    boolean isFinished();
    String getName();
    IBotBreakPattern configure(int radius); // новый метод
}
