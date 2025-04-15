package com.devone.bot.core.logic.task.teleport.params;

import com.devone.bot.core.logic.task.params.BotCoordinate3DParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotTeleportTaskParams extends BotCoordinate3DParams {
    public BotCoordinate3D target = new BotCoordinate3D(0,0,0);

    private String icon = "🗲";
    private String objective = "Teleport";
    
    public BotTeleportTaskParams(BotCoordinate3D target) {
        super(BotTeleportTaskParams.class.getSimpleName());
        this.target = target;
        setIcon(icon);
        setObjective(objective);
    }

    public BotTeleportTaskParams() {
        super(BotTeleportTaskParams.class.getSimpleName());
        setIcon(icon);
        setObjective(objective);
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
        super.setDefaults();
        return this;
    }

    @Override
    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);
        target = ((BotTeleportTaskParams)source).getTarget();
        return this;
    }
}
