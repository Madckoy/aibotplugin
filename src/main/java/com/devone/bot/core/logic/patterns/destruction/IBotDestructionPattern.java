package com.devone.bot.core.logic.patterns.destruction;

import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotAxisDirection.AxisDirection;

public interface IBotDestructionPattern {
    IBotDestructionPattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius, AxisDirection breakDirection);
    BotCoordinate3D findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<BotCoordinate3D> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
