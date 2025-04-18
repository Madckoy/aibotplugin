package com.devone.bot.core.bot.brain.logic.utils.zone;

import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;

public class BotProtectedZone {
    private final double x, y, z;
    private final int radius;

    public BotProtectedZone(double x, double y, double z, int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public boolean isInside(BotLocation location) {
        double dx = location.getX() - x;
        double dy = location.getY() - y;
        double dz = location.getZ() - z;
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public int getRadius() { return radius; }

    @Override
    public String toString() {
        return "Zone at (" + x + ", " + y + ", " + z + ") with radius " + radius;
    }
}
