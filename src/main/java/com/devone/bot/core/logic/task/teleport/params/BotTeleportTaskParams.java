package com.devone.bot.core.logic.task.teleport.params;

import com.devone.bot.core.logic.task.params.BotCoordinate3DParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotTeleportTaskParams extends BotCoordinate3DParams {
    public BotCoordinate3D target;

    private String icon = "🗲";
    private String objective = "Teleport";
    
    public BotTeleportTaskParams(BotCoordinate3D target) {
        super(BotTeleportTaskParams.class.getSimpleName());
        this.target = target;
        setDefaults();
    }
    public BotTeleportTaskParams() {
        super(BotTeleportTaskParams.class.getSimpleName());
        this.target = new BotCoordinate3D(0,0,0);
        setDefaults();
    }
    public BotCoordinate3D getTarget() {
        return target;
    }
    public void setTarget(BotCoordinate3D target) {
        this.target = target;
    }

    @Override
    public Object setDefaults() {
        config.set("teleport.icon", icon);
        config.set("teleport.objective", objective);
        super.setDefaults();
        return this;
    }

    @Override
    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        icon = ((BotTeleportTaskParams)source).getIcon();
        objective = ((BotTeleportTaskParams)source).getObjective();
        target = ((BotTeleportTaskParams)source).getTarget();
        return this;
    }
}
