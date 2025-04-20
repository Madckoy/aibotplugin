package com.devone.bot.core.bot.task.active.excavate.patterns;

import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.utils.blocks.BotLocation;

public interface IBotExcavatePattern {
    IBotExcavatePattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius);
    BotLocation findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<BotLocation> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
