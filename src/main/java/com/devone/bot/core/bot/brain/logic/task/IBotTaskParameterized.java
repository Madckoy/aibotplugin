package com.devone.bot.core.bot.brain.logic.task;

import com.devone.bot.core.bot.brain.logic.task.params.IBotTaskParams;

public interface IBotTaskParameterized<T extends IBotTaskParams> {
    IBotTaskParameterized<T> setParams(T params);
}