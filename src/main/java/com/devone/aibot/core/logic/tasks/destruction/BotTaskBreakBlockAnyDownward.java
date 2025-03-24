package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;

import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAnyDownward extends BotTaskBreakBlock {

    public BotTaskBreakBlockAnyDownward(Bot bot) {
        super(bot);


        setName("🧑‍🔧");
        setTargetMaterials(null);
        setScanMode(ScanMode.DOWNWARD);
        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        // Передай в configure строку с именем yaml-файла
        this.configure(null, 0, 10, true, false, null, "cube.yml");
    }
}
