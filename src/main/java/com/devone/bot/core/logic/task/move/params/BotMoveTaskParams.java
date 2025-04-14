package com.devone.bot.core.logic.task.move.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotMoveTaskParams extends BotTaskParams { 
    // 🆕 Параметры задачи движения
    private BotCoordinate3D target;
    private float speed;

    private String icon = "🏃🏻‍♂️‍➡️";
    private String objective = "Move";


    public BotMoveTaskParams(BotCoordinate3D target, float speed) {
        super(BotMoveTaskParams.class.getSimpleName());
        this.target = target;
        this.speed = speed;
        setDefaults();
    }

    public BotMoveTaskParams(BotCoordinate3D target) {
        super(BotMoveTaskParams.class.getSimpleName());
        this.target = target;
        this.speed = 1.0F;
        setDefaults();
    }

    public BotMoveTaskParams() {
        super(BotMoveTaskParams.class.getSimpleName());
        this.target = null;
        this.speed = 1.0F;
        setDefaults();
    }

    public BotCoordinate3D getTarget() {
        return target;
    }

    public void setTarget(BotCoordinate3D target) {
        this.target = target;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    @Override
    public Object setDefaults() {
        config.set("move.icon", this.icon);
        config.set("move.objective", this.objective);
        config.set("move.speed", this.speed);
        super.setDefaults();
        return this;
    }
    @Override
    public Object copyFrom(IBotTaskParams source) {
        super.copyFrom(source);

        setIcon(((BotMoveTaskParams)source).getIcon());
        setObjective(((BotMoveTaskParams)source).getObjective());
        
        speed = ((BotMoveTaskParams)source).getSpeed();
        return this;
    }
   
    @Override
    public String toString() {
        return "BotMoveTaskParams{" +
                "target=" + target +
                ", speedMultiplier=" + speed +
                '}';
    }


}
