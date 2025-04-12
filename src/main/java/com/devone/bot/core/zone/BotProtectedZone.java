package com.devone.bot.core.zone;

import com.devone.bot.utils.blocks.BotCoordinate3D;

public class BotProtectedZone {
    private final double x, y, z;
    private final int radius;

    public BotProtectedZone(double x, double y, double z, int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public boolean isInside(BotCoordinate3D location) {
        double dx = location.x - x;
        double dy = location.y - y;
        double dz = location.z - z;
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
