package com.devone.bot.core.utils.pattern;
import java.util.List;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.pattern.params.BotPatternRunnerParams;

public interface IBotPatternRunner {

    IBotPatternRunner setParams(BotPatternRunnerParams params);
    
    boolean checkIfLoaded();

    IBotPatternRunner load(BotPosition obs);

    BotPosition       getNextSolid();
    BotPosition       getNextVoid();
    
    List<BotPosition> getAll();

    List<BotPosition> getAllSolid();
    List<BotPosition> getAllVoid();

    boolean           isNoVoid();
    boolean           isNoSolid();

    String            getName();

}
