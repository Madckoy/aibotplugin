package com.devone.bot.core.utils.blocks;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData {

    private BotPosition position = new BotPosition();
    private String type;
    private UUID uuid;
    private String tag;

    public BotBlockData() {
    }

    public BotBlockData(int x, int y, int z) {
        this.position = new BotPosition(x, y, z);
    }

    public BotPosition getPosition() {
        return position;
    }

    public void setPosition(BotPosition position) {
        this.position = position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
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
        return BotEntityUtils.isHostile(this.type);
    }

    @JsonIgnore
    public boolean isPeaceful() {
        return BotEntityUtils.isPeaceful(this.type);
    }

    @JsonIgnore
    public int getX() {
        return (int)position.getX();
    }

    @JsonIgnore
    public int getY() {
        return (int)position.getY();
    }

    @JsonIgnore
    public int getZ() {
        return (int)position.getZ();
    }

    @Override
    public String toString() {
        return String.format("Block: [x=%d, y=%d, z=%d, type=%s, tag=%s]", getX(), getY(), getZ(), type, tag);
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
}
