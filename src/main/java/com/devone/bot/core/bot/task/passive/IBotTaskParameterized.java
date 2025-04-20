package com.devone.bot.core.bot.task.passive;

import com.devone.bot.core.bot.task.passive.params.IBotTaskParams;

public interface IBotTaskParameterized<T extends IBotTaskParams> {
    IBotTaskParameterized<T> setParams(T params);
}