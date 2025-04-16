package com.devone.bot.core.logic.task.sonar;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.sonar.params.BotSonarTaskParams;
import com.devone.bot.utils.scene.BotSceneData;
import com.devone.bot.utils.scene.BotSceneScan3D;

public class BotSonar3DTask extends BotTask<BotSonarTaskParams> {

    private int radius;
    private int height;

    public BotSonar3DTask(Bot bot) {
        super(bot);
        // Загружаем параметры по умолчанию
        setParams(new BotSonarTaskParams());
    }

    public BotSonar3DTask(Bot bot, int radius, int height) {
        super(bot);
        // Загружаем параметры и переопределяем вручную
        BotSonarTaskParams params = new BotSonarTaskParams();
        params.setRadius(radius);
        params.setHeight(height);
        setParams(params);
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
        BotSceneData scene = BotSceneScan3D.scan(bot, radius, height);
        bot.getRuntimeStatus().setSceneData(scene);
        stop();
    }
}
