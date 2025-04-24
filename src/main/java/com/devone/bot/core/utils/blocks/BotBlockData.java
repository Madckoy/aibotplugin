package com.devone.bot.core.utils.blocks;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotPosition {

    private String type;
    private UUID   uuid;
    private String tag;

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
    public String getTag() {
        return tag;
    }

    @JsonIgnore
    public void setTag(String tag) {
        this.tag = tag;
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
    public String toString() {
        return String.format("Block: [ x=%d, y=%d, z=%d, type=%s, bot=%s ]", this.getX(), this.getY(), this.getZ(), type, tag);
    }
    @JsonIgnore
    public BotPosition getPosition(){
        return new BotPosition(getX(), getY(), getZ());
    }

    @Override
    public BotBlockData clone() {
        BotBlockData copy = new BotBlockData();
        copy.setX(this.getX());
        copy.setY(this.getY());
        copy.setZ(this.getZ());
        copy.setType(this.getType());
        copy.setUUID(this.getUUID());
        copy.setTag(this.getTag());
        return copy;
    }

    @JsonIgnore
    public boolean isHostile() {
        return BotEntityUtils.isHostile(this.type);
    }

    @JsonIgnore
    public boolean isPeaceful() {
        return BotEntityUtils.isPeaceful(this.type);
    }
}