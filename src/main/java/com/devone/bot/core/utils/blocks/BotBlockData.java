package com.devone.bot.core.utils.blocks;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData {

    private BotPosition position;
    private String type;
    private UUID uuid;
    private String tag;

    public BotBlockData() {
        this.position = new BotPosition();
    }

    public BotBlockData(double x, double y, double z) {
        this.position = new BotPosition(x, y, z);
    }

    public BotPosition getPosition() {
        return position;
    }

    public void setPosition(BotPosition pos) {
        this.position = pos;
    }

    public double getX() {
        return position.getX();
    }

    public void setX(double x) {
        position.setX(x);
    }

    public double getY() {
        return position.getY();
    }

    public void setY(double y) {
        position.setY(y);
    }

    public double getZ() {
        return position.getZ();
    }

    public void setZ(double z) {
        position.setZ(z);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setUUID(UUID id) {
        this.uuid = id;
    }

    public UUID getUUID() {
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
    public boolean isHostile() {
        return true;
    }

    @JsonIgnore
    public boolean isPeaceful() {
        return false;
    }

    @Override
    public BotBlockData clone() {
        BotBlockData copy = new BotBlockData();
        copy.setPosition(new BotPosition(this.getPosition()));
        copy.setType(this.getType());
        copy.setUUID(this.getUUID());
        copy.setTag(this.getTag());
        return copy;
    }

    @Override
    public String toString() {
        return String.format("Block: [ x=%.2f, y=%.2f, z=%.2f, type=%s, tag=%s ]",
            getX(), getY(), getZ(), type, tag);
    }
}
