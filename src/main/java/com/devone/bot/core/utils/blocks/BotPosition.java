package com.devone.bot.core.utils.blocks;

public class BotPosition {
    private double x, y, z;

    public BotPosition() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public BotPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BotPosition(BotPosition other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public BotPositionKey toKey() {
        return new BotPositionKey(this);
    }

    public double distanceTo(BotPosition other) {
        if (other == null) return Double.MAX_VALUE;
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("BotPosition[%.2f, %.2f, %.2f] → key=%s",
            x, y, z, toKey().toString());
    }

    public String toCompactString() {
        return String.format("%d,%d,%d", (int) x, (int) y, (int) z);
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
}
