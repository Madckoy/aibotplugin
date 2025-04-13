package com.devone.bot.core.logic.task.teleport.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotTeleportTaskParams extends BotTaskParams {
    public BotCoordinate3D target;
    
    public BotTeleportTaskParams(BotCoordinate3D target) {
        this.target = target;
    }
    public BotTeleportTaskParams() {
        this.target = null;
    }
    public BotCoordinate3D getTarget() {
        return target;
    }
    public void setTarget(BotCoordinate3D target) {
        this.target = target;
    }
}
