package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskBreakBlockConfig;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

public class BotTaskBreakBlockAnyUpward extends BotTaskBreakBlock {

    public BotTaskBreakBlockAnyUpward(Bot bot) {
        super(bot);

        setName(getName()+"▲");

        setTargetMaterials(null);
        setScanMode(ScanMode.UPWARD);
        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotTaskBreakBlockConfig config = new BotTaskBreakBlockConfig("BotTaskBreakBlockAnyUpwards.yml");
        // Передай в configure строку с именем yaml-файла
        this.configure(null, 0, getBreakRadius(), true, false, null, config.getPattern());
    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
