package com.devone.bot.core.bot.brain.logic.utils.blocks;

import java.util.UUID;

import org.bukkit.entity.EntityType;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotLocation {

    private String type;
    private UUID   uuid;

    public void setType( String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setUUID( UUID id){
        this.uuid = id;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    @JsonIgnore
    private boolean bot;  // из JSON  
    
    @JsonIgnore
    public boolean isBot() {
        return bot;
    }

    @JsonIgnore
    public void setBot(boolean bot) {
        this.bot = bot;
    }

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
    public EntityType toEntityType() {
        if (type == null) return null;
        try {
            return EntityType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
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
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", this.getX(), this.getY(), this.getZ(), type, bot);
    }
    @JsonIgnore
    public BotLocation getLocation(){
        return new BotLocation(getX(), getY(), getZ());
    }
}