package com.devone.bot.core.bot.behaviour.task;

import com.devone.bot.core.bot.behaviour.task.params.IBotTaskParams;

public interface IBotTaskParameterized<T extends IBotTaskParams> {
    IBotTaskParameterized<T> setParams(T params);
}