package com.devone.bot.core.logic.tasks.sonar;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.scene.BotSceneData;
import com.devone.bot.utils.scene.BotSceneScan3D;


public class BotSonar3DTask extends BotTask {

    private BotTask parent;
    private int radius;
    private int height;

    public BotSonar3DTask(Bot bot, BotTask caller, int radius, int height) {
        super(bot, "𖣠"); // ᯤ
        parent = caller;
        this.radius = radius;
        this.height = radius; // full range scan

        setObjective("Scan Signatures");
    }

    // Метод конфигурации для установки ScanMode
    @Override
    public BotSonar3DTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams)params);
        return this;
    }

    @Override
    public void execute() {

        setObjective("Scanning Signatures...");
        BotSceneData scene = BotSceneScan3D.scan(bot, radius, this.height); // full range scan
        parent.setSceneData(scene);

        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;
    }

}