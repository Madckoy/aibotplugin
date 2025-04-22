package com.devone.bot.core.task.passive.active.sonar;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.scene.BotSceneData;
import com.devone.bot.core.brain.memory.scene.BotSceneScan3D;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.passive.active.sonar.params.BotSonarTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;

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

        BotLogger.debug(icon, isLogging(), bot.getId() + " 📡 Performing 3D sonar scan with radius=" + radius + ", height=" + height);

        BotSceneData sceneData = BotSceneScan3D.scan(bot, radius, height);

        bot.getBrain().getMemory().setSceneData(sceneData);
        
        stop();
    }

}
