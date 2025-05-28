package com.devone.bot.core.utils.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData {

    private BotPosition position;
    private String type;
    private UUID uuid;

    private List<String> tags = new ArrayList<>();

    public BotBlockData() {
        this.position = new BotPosition();
    }

    public BotBlockData(int x, int y, int z) {
        this.position = new BotPosition(x, y, z);
    }
    public BotBlockData(double x, double y, double z) {
        this();
        this.position = new BotPosition(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public BotPosition getPosition() {
        return position;
    }

    public void setPosition(BotPosition pos) {
        this.position = pos;
    }

    public int getX() {
        return (int) Math.floor(position.getX());
    }

    public void setX(int x) {
        position.setX(x);
    }

    public int getY() {
        return (int) Math.floor(position.getY());
    }

    public void setY(int y) {
        position.setY(y);
    }

    public int getZ() {
        return (int) Math.floor(position.getZ());
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = (tags != null) ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public void addTag(String tagName) {
        if (tagName != null && !tagName.trim().isEmpty()) {
            if (!tags.contains(tagName)) {
                tags.add(tagName);
            }
        }
    }

    public boolean hasTag(String tagName) {
        return tagName != null && tags.contains(tagName);
    }
    
    @JsonIgnore
    public BotPositionKey toPositionKey() {
        return position.toPositionKey();
    }

    public boolean isSameBlock(BotBlockData other) {
        return other != null && this.toPositionKey().equals(other.toPositionKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotBlockData)) return false;
        BotBlockData that = (BotBlockData) o;
        return Objects.equals(this.toPositionKey(), that.toPositionKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toPositionKey());
    }

    public int distanceTo(BotBlockData other) {
        if (other == null) return Integer.MAX_VALUE;
        int dx = getX() - other.getX();
        int dy = getY() - other.getY();
        int dz = getZ() - other.getZ();
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
    }
    
    @Override
    public BotBlockData clone() {
        BotBlockData copy = new BotBlockData();
        copy.setPosition(new BotPosition(this.getPosition())); // глубокая копия позиции
        copy.setType(this.getType());                          // копия типа
        copy.setUUID(this.getUUID());                          // UUID
        copy.setTags(new ArrayList<>(this.getTags()));         // копия всех тегов
        return copy;
    }

    @Override
    public String toString() {
        return String.format("%d, %d, %d, %s, tags=%s",
                getX(), getY(), getZ(), type, tags);
    }

    public String toCompactString() {
        return String.format("%d, %d, %d", getX(), getY(), getZ());
    }
}
