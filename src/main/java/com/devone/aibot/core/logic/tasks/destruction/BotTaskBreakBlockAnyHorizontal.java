package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAnyHorizontal extends BotTaskBreakBlock {

    public BotTaskBreakBlockAnyHorizontal(Bot bot) {
        super(bot);

        setName("⛏");
        setTargetMaterials(null);
        setScanMode(ScanMode.HORIZONTAL_SLICE);
        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
