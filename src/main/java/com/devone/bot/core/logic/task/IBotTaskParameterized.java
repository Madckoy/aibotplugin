package com.devone.bot.core.logic.task;

import com.devone.bot.core.logic.task.params.IBotTaskParams;

public interface IBotTaskParameterized<T extends IBotTaskParams> {
    IBotTaskParameterized<T> setParams(T params);
}