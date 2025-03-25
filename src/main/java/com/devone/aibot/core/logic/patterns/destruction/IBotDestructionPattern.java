package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.Bot3DCoordinate;

import java.util.List;

public interface IBotDestructionPattern {
    IBotDestructionPattern configure(int radius);
    Bot3DCoordinate findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<Bot3DCoordinate> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
