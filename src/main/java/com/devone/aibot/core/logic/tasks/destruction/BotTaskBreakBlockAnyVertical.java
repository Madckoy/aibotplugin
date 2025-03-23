package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAnyVertical extends BotTaskBreakBlock {

    public BotTaskBreakBlockAnyVertical(Bot bot) {
        super(bot);
        setName("⛏");
        setTargetMaterials(null); // Все блоки
        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
        setScanMode(ScanMode.VERTICAL_SLICE);
    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
