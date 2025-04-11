package com.devone.bot.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotCoordinate3D {


    public String type;

    @JsonIgnore
    public boolean bot;  // из JSON  
    
    @JsonIgnore
    public boolean isAir() {
        return type != null && BlockMaterialUtils.AIR_TYPES.contains(type.toUpperCase());
    } 

    @JsonIgnore
    public boolean isCover() {
        return type != null && BlockMaterialUtils.COVER_TYPES.contains(type.toUpperCase());
    }   

    @JsonIgnore   
    public boolean isDangerous() {
        return type != null && BlockMaterialUtils.UNSAFE_TYPES.contains(type.toUpperCase());  
    } 
    
    @JsonIgnore
    public boolean isBot() {
        return bot;
    }

    @JsonIgnore
    public boolean isUnknown() {
        return type != null && BlockMaterialUtils.UNSAFE_TYPES.contains(type.toUpperCase());
    }
    
    @JsonIgnore
    public boolean isPassable() {
        return type != null && !type.isBlank()
               && !BlockMaterialUtils.NON_PASSABLE_BLOCKS.contains(type.toUpperCase());
    }
    
    @JsonIgnore
    public BotCoordinate3D getCoordinate3D() {
        return (BotCoordinate3D)this;
    }
    
    @JsonIgnore
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}