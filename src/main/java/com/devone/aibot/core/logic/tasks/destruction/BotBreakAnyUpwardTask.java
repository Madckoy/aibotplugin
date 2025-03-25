package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotBreakBlockTaskConfig;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;

public class BotBreakAnyUpwardTask extends BotBreakTask {

    public BotBreakAnyUpwardTask(Bot bot) {
        super(bot);

        setName(getName()+"(▲)");

        setTargetMaterials(null);
        setScanMode(ScanMode.UPWARD);
        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());

        BotBreakBlockTaskConfig config = new BotBreakBlockTaskConfig("BotBreakAnyUpwardTask.yml");
        // Передай в configure строку с именем yaml-файла
        this.configure(null, 0, getBreakRadius(), true, false, null, config.getPattern());
    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
