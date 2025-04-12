package com.devone.bot.core.logic.tasks.excavate.patterns;

import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;

public interface IBotExcavatePattern {
    IBotExcavatePattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius, AxisDirection breakDirection);
    BotCoordinate3D findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<BotCoordinate3D> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
