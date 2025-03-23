package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;

import java.util.List;

public interface IBotDestructionPattern {
    IBotDestructionPattern configure(int radius);
    Location findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<Location> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
