package com.devone.bot.core.bot.task.active.excavate.patterns;

import java.util.List;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.utils.blocks.BotLocation;

public interface IBotExcavatePatternRunner {
    IBotExcavatePatternRunner configure(int offsetOuterX, int offsetOuterY, int offsetOuterZ, int outerRadius, 
                                  int offsetInnerX, int offsetInnerY, int offsetInnerZ, int innerRadius, boolean invereted);

    BotLocation findNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<BotLocation> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
