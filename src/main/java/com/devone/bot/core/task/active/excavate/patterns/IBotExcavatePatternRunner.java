package com.devone.bot.core.task.active.excavate.patterns;

import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.excavate.patterns.generator.params.BotExcavateTemplateRunnerParams;
import com.devone.bot.core.utils.blocks.BotPosition;

public interface IBotExcavatePatternRunner {
    IBotExcavatePatternRunner setParams(BotExcavateTemplateRunnerParams params);
    BotPosition getNextBlock(Bot bot);
    boolean isFinished();
    String getName();
    List<BotPosition> getAllPlannedBlocks(); // опционально, для отладки или дебага
}
