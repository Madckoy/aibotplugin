package com.devone.bot.core.utils.blocks;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotPosition {

    private String type;
    private UUID   uuid;
    private String notes;

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
    public String getNotes() {
        return notes;
    }

    @JsonIgnore
    public void setNotes(String notes) {
        this.notes = notes;
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
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", this.getX(), this.getY(), this.getZ(), type, bot);
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
        copy.setNotes(this.getNotes());
        copy.setBot(this.isBot());
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