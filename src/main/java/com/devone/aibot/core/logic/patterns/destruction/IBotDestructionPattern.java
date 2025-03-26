package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.Bot3DCoordinate;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;

import java.util.List;

public interface IBotDestructionPattern {
    IBotDestructionPattern configure(int radius, AxisDirection direction);
    Bot3DCoordinate findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<Bot3DCoordinate> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
