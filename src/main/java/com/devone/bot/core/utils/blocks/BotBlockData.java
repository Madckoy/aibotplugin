package com.devone.bot.core.utils.blocks;

import java.util.Objects;
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

    public BotBlockData(int x, int y, int z) {
        this.position = new BotPosition(x, y, z);
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

    public int getX() {
        return position.getX(); // теперь безопасно, уже floored
    }

    public void setX(int x) {
        position.setX(x);
    }

    public int getY() {
        return position.getY();
    }

    public void setY(int y) {
        position.setY(y);
    }

    public int getZ() {
        return position.getZ();
    }

    public void setZ(int z) {
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
    public BotPositionKey toKey() {
        return position.toKey();
    }

    public boolean isSameBlock(BotBlockData other) {
        return other != null && this.toKey().equals(other.toKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotBlockData)) return false;
        BotBlockData that = (BotBlockData) o;
        return Objects.equals(this.toKey(), that.toKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toKey());
    }

    public int distanceTo(BotBlockData other) {
        if (other == null) return Integer.MAX_VALUE;
        int dx = getX() - other.getX();
        int dy = getY() - other.getY();
        int dz = getZ() - other.getZ();
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
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
        return String.format("%d, %d, %d, %s, %s",
            getX(), getY(), getZ(), type, tag);
    }

    public String toCompactString() {
        return String.format("%d, %d, %d", getX(), getY(), getZ());
    }
}
