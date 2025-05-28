package com.devone.bot.core.utils.blocks;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotPosition {
    private double x, y, z;
    private float pitch;
    private float yaw;

    public BotPosition() {
        this(0, 0, 0);
    }

    public BotPosition(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
    }

    public BotPosition(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public BotPosition(BotPosition other) {
        this(other.x, other.y, other.z, other.yaw, other.pitch);
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }

    // --- Setters ---
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }

    // --- Helpers ---
    public BotPositionKey toPositionKey() {
        return new BotPositionKey(getX(), getY(), getZ());
    }
    
    public BotBlockData toBlockDataKey() {
        return new BotBlockData(getX(), getY(), getZ());
    }

    public BotPosition add(double dx, double dy, double dz) {
        return new BotPosition(this.x + dx, this.y + dy, this.z + dz, this.yaw, this.pitch);
    }

    public BotPosition clone() {
        return new BotPosition(this);
    }

    // --- Distance ---
    public double distanceTo(BotPosition other) {
        if (other == null) return Double.MAX_VALUE;
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceTo(BotBlockData other) {
        if (other == null) return Double.MAX_VALUE;
        return distanceTo(other.getPosition());
    }

    public int blockDistanceTo(BotBlockData other) {
        return (other == null) ? Integer.MAX_VALUE : blockDistanceTo(other.getPosition());
    }

    public int blockDistanceTo(BotPosition other) {
        if (other == null) return Integer.MAX_VALUE;
        int dx = (int) Math.floor(x) - (int) Math.floor(other.x);
        int dy = (int) Math.floor(y) - (int) Math.floor(other.y);
        int dz = (int) Math.floor(z) - (int) Math.floor(other.z);
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // --- Output ---
    @Override
    public String toString() {
        return String.format("%.2f, %.2f, %.2f", x, y, z);
    }

    public String toCompactString() {
        return String.format("%.1f, %.1f, %.1f", x, y, z);
    }

    // --- Equals & Hash ---
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BotPosition)) return false;
        BotPosition other = (BotPosition) obj;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0 &&
               Float.compare(yaw, other.yaw) == 0 &&
               Float.compare(pitch, other.pitch) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        result = 31 * result + Float.hashCode(yaw);
        result = 31 * result + Float.hashCode(pitch);
        return result;
    }
}
