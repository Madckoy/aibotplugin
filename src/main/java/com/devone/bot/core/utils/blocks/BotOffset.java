package com.devone.bot.core.utils.blocks;

public class BotOffset {
    private double x, y, z;

    public BotOffset() {
        this(0, 0, 0);
    }

    public BotOffset(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BotOffset(BotOffset other) {
        this(other.x, other.y, other.z);
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    // --- Setters ---
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    // --- Output ---
    @Override
    public String toString() {
        return String.format("%.2f, %.2f, %.2f", x, y, z);
    }

    public String toCompactString() {
        return String.format("%.1f, %.1f, %.1f", x, y, z);
    }

}
