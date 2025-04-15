package com.devone.bot.core.logic.task.sonar.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotSonarTaskParams extends BotTaskParams {

    private int radius = BotConstants.DEFAULT_SCAN_RANGE;
    private int height = BotConstants.DEFAULT_SCAN_RANGE;
    private String icon = "𖣠";
    private String objective = "Scan Signatures";

    public BotSonarTaskParams(BotCoordinate3D target) {
        super(BotSonarTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }
    public BotSonarTaskParams() {
        super(BotSonarTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public String getIcon() {
        return icon;
    }

    public String getObjective() {
        return objective;
    }
    public int getRadius() {
        return radius;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Object setDefaults() {
        config.set("sonar.radius", this.radius);
        config.set("sonar.height", height);

        super.setDefaults();
        return this;
    }

    @Override
    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        radius = ((BotSonarTaskParams)source).getRadius();
        height = ((BotSonarTaskParams)source).getHeight();
        return this;
    }
}
