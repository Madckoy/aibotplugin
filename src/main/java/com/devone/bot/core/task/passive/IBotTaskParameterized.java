package com.devone.bot.core.task.passive;

import com.devone.bot.core.task.passive.params.IBotTaskParams;

public interface IBotTaskParameterized<T extends IBotTaskParams> {
    IBotTaskParameterized<T> setParams(T params);
}