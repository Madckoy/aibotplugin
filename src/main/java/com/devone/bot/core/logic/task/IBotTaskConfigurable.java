package com.devone.bot.core.logic.task;

import com.devone.bot.core.logic.task.params.IBotTaskParams;

public interface IBotTaskConfigurable {

    Object configure(IBotTaskParams params);

}