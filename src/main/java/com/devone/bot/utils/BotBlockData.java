package com.devone.bot.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotCoordinate3D {

    public String type;

     @JsonIgnore
    public boolean bot;  // из JSON  

    public boolean isAir() {
        return type != null && BlockMaterialUtils.AIR_TYPES.contains(type.toUpperCase());
    } 

    public boolean isCover() {
        return type != null && BlockMaterialUtils.COVER_TYPES.contains(type.toUpperCase());
    }   
    
    public boolean isDangerous() {
        return type != null && BlockMaterialUtils.UNSAFE_TYPES.contains(type.toUpperCase());
    } 

    public BotCoordinate3D getCoordinate3D() {
        return new BotCoordinate3D(x, y, z);
    }

    public boolean isSolid() {
        // сюда можно добавлять исключения по мере надобности
        if (type == null) return false;
        String t = type.toUpperCase();
        return !(
            t.contains("AIR") ||
            t.contains("WATER") ||
            t.contains("LAVA") ||

            t.equals("FIRE") ||
            t.equals("CACTUS")
        );
    }

    public boolean isBot() {
        return bot;
    }

    public boolean isPassableAndSafe() {
        if (type == null) return false;
        String t = type.toUpperCase();
        return BlockMaterialUtils.AIR_TYPES.contains(t) || t.equals("TALL_GRASS") || t.equals("SNOW") || t.equals("FLOWER");
    }

    @Override
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}