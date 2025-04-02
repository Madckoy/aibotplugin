package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotCoordinate3D;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;

import java.util.List;

public interface IBotDestructionPattern {
    IBotDestructionPattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius, AxisDirection breakDirection);
    BotCoordinate3D findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<BotCoordinate3D> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
