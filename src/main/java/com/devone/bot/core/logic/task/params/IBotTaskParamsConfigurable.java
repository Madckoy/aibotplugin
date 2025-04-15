package com.devone.bot.core.logic.task.params;

import java.io.File;

public interface IBotTaskParamsConfigurable {
    Object setDefaults();
    Object saveDefaultFile();
    Object loadFile(File fl, String str);

}