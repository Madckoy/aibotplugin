package com.devone.bot.utils.blocks;

import org.bukkit.entity.EntityType;

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

    public EntityType toEntityType() {
        return EntityType.valueOf(type.toUpperCase());
    }

    @JsonIgnore
    public boolean isHostileMob() {
        return BotEntityUtils.isHostileMob(this.type);
    }

    @JsonIgnore
    public boolean isPassiveMob() {
        return BotEntityUtils.isPassiveMob(this.type);
    }
    
    @JsonIgnore
    public BotCoordinate3D getCoordinate3D() {
        return new BotCoordinate3D(x,y,z);
    }
    
    @JsonIgnore
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}