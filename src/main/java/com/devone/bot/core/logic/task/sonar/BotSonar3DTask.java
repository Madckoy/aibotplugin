package com.devone.bot.core.logic.task.sonar;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.core.logic.task.sonar.params.BotSonarTaskParams;
import com.devone.bot.utils.scene.BotSceneData;
import com.devone.bot.utils.scene.BotSceneScan3D;


public class BotSonar3DTask extends BotTask {
    private BotSonarTaskParams params = new BotSonarTaskParams();
    private int radius;
    private int height;

    public BotSonar3DTask(Bot bot) {
        super(bot);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        this.radius = params.getRadius();
        this.height = params.getHeight();
    }

    public BotSonar3DTask(Bot bot, int radius, int height) {
        super(bot);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        this.radius = radius;
        this.height = height;
    }

    @Override
    public BotSonar3DTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams)params);
        this.params.copyFrom(params);
        this.radius = ((BotSonarTaskParams)params).getRadius();
        this.height = ((BotSonarTaskParams)params).getHeight();
        return this;
    }

    @Override
    public void execute() {
        BotSceneData scene = BotSceneScan3D.scan(bot, this.radius, this.height); // full range scan
        bot.getRuntimeStatus().setSceneData(scene);
        stop();
    }

}