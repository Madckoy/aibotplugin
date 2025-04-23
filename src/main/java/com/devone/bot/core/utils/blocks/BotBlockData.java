package com.devone.bot.core.utils.blocks;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.entity.EntityType;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotPosition {

    private String type;
    private UUID uuid;
    private boolean targetable;

    @JsonIgnore
    public boolean isTargetable() {
        return targetable;
    }

    @JsonIgnore
    public void setTargetable(boolean targetable) {
        this.targetable = targetable;
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
    private boolean bot; // из JSON

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
        if (type == null)
            return null;
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
        return String.format("Block[ %d, %d, %d, %s, %b ]", this.getX(), this.getY(), this.getZ(), type, bot);
    }

    @JsonIgnore
    public BotPosition getLocation() {
        return new BotPosition(getX(), getY(), getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        // Сравниваем с чем угодно, что наследует BotLocation
        if (!(o instanceof BotPosition)) return false;

        BotPosition other = (BotPosition) o;
        return this.getX() == other.getX()
            && this.getY() == other.getY()
            && this.getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }
}