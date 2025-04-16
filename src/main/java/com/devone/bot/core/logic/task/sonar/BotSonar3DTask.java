package com.devone.bot.core.logic.task.sonar;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.sonar.params.BotSonarTaskParams;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.scene.BotSceneData;
import com.devone.bot.utils.scene.BotSceneScan3D;

public class BotSonar3DTask extends BotTaskAutoParams<BotSonarTaskParams> {

    private int radius;
    private int height;

    public BotSonar3DTask(Bot bot) {
        super(bot, BotSonarTaskParams.class);
    }

    public BotSonar3DTask(Bot bot, int radius, int height) {
        this(bot);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public IBotTaskParameterized<BotSonarTaskParams> setParams(BotSonarTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        this.radius = params.getRadius();
        this.height = params.getHeight();
        return this;
    }

    @Override
    public void execute() {
        BotLogger.info("📡", isLogging(),
                bot.getId() + " Performing 3D sonar scan with radius=" + radius + ", height=" + height);
        BotSceneData scene = BotSceneScan3D.scan(bot, radius, height);
        bot.getRuntimeStatus().setSceneData(scene);
        stop();
    }

}
