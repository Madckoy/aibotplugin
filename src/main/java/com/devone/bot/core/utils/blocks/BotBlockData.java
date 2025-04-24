package com.devone.bot.core.utils.blocks;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData  {

    private BotPosition position;
    private String type;
    private UUID   uuid;
    private String tag;

    public BotBlockData() {
        position = new BotPosition();
    }

    public BotBlockData(int x, int y, int z) {
        this.position = new BotPosition(x, y, z);
    }

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

    @Override
    public BotBlockData clone() {
        BotBlockData copy = new BotBlockData();
        copy.setPosition(this.getPosition());
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

    public BotPosition getPosition() {
        return position;
    }

    public void setPosition(BotPosition pos) {
       position = pos;
    }


    public int getX() { return position.getX(); }
    public void setX(int x) { this.position = new BotPosition(x, getY(), getZ()); }

    public int getY() { return position.getY(); }
    public void setY(int y) { this.position = new BotPosition(getX(), y, getZ()); }

    public int getZ() { return position.getZ(); }
    public void setZ(int z) { this.position = new BotPosition(getX(), getY(), z); }
    
}