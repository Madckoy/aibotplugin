package com.devone.bot.core.logic.task.params;
import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotCoordinate3DParams extends BotTaskParams{

    private BotCoordinate3D loc = new BotCoordinate3D(0,0,0);

    public BotCoordinate3DParams() {
        super(BotCoordinate3DParams.class.getSimpleName());
        setDefaults();
    }

    public BotCoordinate3DParams(String f_name) {
        super(f_name);
        setDefaults();
    }

    public Object setDefaults() {

        config.set("coordinate.x", loc.x);
        config.set("coordinate.y", loc.y);
        config.set("coordinate.z", loc.z);

        super.setDefaults();
        return this;
    }

    public Object copyFrom(IBotTaskParams source) {

        loc.x = ((BotCoordinate3DParams)source).getX();
        loc.y = ((BotCoordinate3DParams)source).getY();
        loc.z = ((BotCoordinate3DParams)source).getZ();
        
        super.copyFrom(source);
        return this;
    }

    public int getX(){
        return loc.x;
    }

    public int getY(){
        return loc.y;
    }
    
    public int getZ(){
        return loc.z;
    }

    public BotCoordinate3D getCoordinate3D() {
        return loc;
    }

}
