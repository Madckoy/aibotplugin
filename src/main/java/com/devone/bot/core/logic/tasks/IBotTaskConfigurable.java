package com.devone.bot.core.logic.tasks;

import com.devone.bot.core.logic.tasks.params.IBotTaskParams;

public interface IBotTaskConfigurable {

    Object configure(IBotTaskParams params);

}