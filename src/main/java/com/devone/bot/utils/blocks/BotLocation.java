package com.devone.bot.utils.blocks;

import java.util.Objects;

public class BotLocation {
    private int x, y, z;

    public BotLocation() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public BotLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BotLocation(BotLocation other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotLocation)) return false;
        BotLocation that = (BotLocation) o;
        return x == that.x && y == that.y && z == that.z;
    }

    public double distanceTo(BotLocation other) {
        if (other == null) return Double.MAX_VALUE;
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        int dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
 
    @Override
    public String toString() {
        return String.format("%d,%d,%d", x, y, z);
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getZ() { return z; }
    public void setZ(int z) { this.z = z; }

}
